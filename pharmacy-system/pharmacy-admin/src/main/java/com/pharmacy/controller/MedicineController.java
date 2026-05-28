package com.pharmacy.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Medicine;
import com.pharmacy.excel.MedicineExcelRow;
import com.pharmacy.mapper.MedicineMapper;
import com.pharmacy.service.MedicineService;
import com.pharmacy.util.ExcelResponseUtil;
import com.pharmacy.vo.ImportResultVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/medicine")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineMapper medicineMapper;

    @PostMapping
    public Result<Medicine> add(@RequestBody Medicine medicine) {
        boolean success = medicineService.save(medicine);
        return success ? Result.success(medicine) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = medicineService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<Medicine> update(@RequestBody Medicine medicine) {
        boolean success = medicineMapper.updateMedicineById(medicine) > 0;
        return success ? Result.success(medicine) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Medicine> getById(@PathVariable Integer id) {
        Medicine medicine = medicineMapper.selectMedicineById(id);
        return medicine != null ? Result.success(medicine) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<Medicine>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String medName,
            @RequestParam(required = false) String medType,
            @RequestParam(required = false) Integer status) {
        log.info("查询药品 - current: {}, size: {}, medName: {}, medType: {}, status: {}", current, size, medName, medType, status);
        Page<Medicine> page = new Page<>(current, size);
        Page<Medicine> result = medicineMapper.selectMedicinePage(page, medName, medType, status);
        log.info("查询结果 - 总记录数: {}, 当前页记录数: {}", result.getTotal(), result.getRecords().size());
        return Result.success(result);
    }

    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String medName,
            @RequestParam(required = false) String medType,
            @RequestParam(required = false) Integer status,
            HttpServletResponse response) throws IOException {
        Page<Medicine> page = new Page<>(1, 10000);
        List<MedicineExcelRow> rows = medicineMapper.selectMedicinePage(page, medName, medType, status)
                .getRecords()
                .stream()
                .map(this::toExcelRow)
                .collect(Collectors.toList());

        ExcelResponseUtil.prepareExcelResponse(response, "药品数据.xlsx");
        EasyExcel.write(response.getOutputStream(), MedicineExcelRow.class)
                .sheet("药品数据")
                .doWrite(rows);
    }

    @GetMapping("/import-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        ExcelResponseUtil.prepareExcelResponse(response, "药品导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), MedicineExcelRow.class)
                .sheet("药品导入模板")
                .doWrite(List.of());
    }

    @PostMapping("/import")
    @Transactional(rollbackFor = Exception.class)
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        ImportResultVO result = new ImportResultVO();
        if (file == null || file.isEmpty()) {
            result.addError("上传文件不能为空");
            return Result.success(result);
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            result.addError("仅支持 .xlsx 或 .xls 文件");
            return Result.success(result);
        }

        List<MedicineExcelRow> rows = EasyExcel.read(file.getInputStream())
                .head(MedicineExcelRow.class)
                .sheet()
                .doReadSync();

        for (int i = 0; i < rows.size(); i++) {
            int rowNo = i + 2;
            MedicineExcelRow row = rows.get(i);
            try {
                Medicine medicine = toMedicine(row);
                if (medicine.getMedId() != null && medicineService.getById(medicine.getMedId()) != null) {
                    medicine.setUpdateTime(LocalDateTime.now());
                    medicineMapper.updateMedicineById(medicine);
                } else {
                    medicine.setCreateTime(LocalDateTime.now());
                    medicine.setUpdateTime(LocalDateTime.now());
                    medicineService.save(medicine);
                }
                result.addSuccess();
            } catch (IllegalArgumentException e) {
                result.addError("第" + rowNo + "行：" + e.getMessage());
            } catch (Exception e) {
                log.error("药品导入第{}行失败", rowNo, e);
                result.addError("第" + rowNo + "行：导入失败，请检查数据格式");
            }
        }
        return Result.success(result);
    }

    private MedicineExcelRow toExcelRow(Medicine medicine) {
        MedicineExcelRow row = new MedicineExcelRow();
        row.setMedId(medicine.getMedId());
        row.setMedName(medicine.getMedName());
        row.setMedAlias(medicine.getMedAlias());
        row.setMedType(medicine.getMedType());
        row.setSpec(medicine.getSpec());
        row.setUnit(medicine.getUnit());
        row.setDosageForm(medicine.getDosageForm());
        row.setManufacturer(medicine.getManufacturer());
        row.setApprovalNo(medicine.getApprovalNo());
        row.setRetailPrice(medicine.getRetailPrice());
        row.setPurchasePrice(medicine.getPurchasePrice());
        row.setIsRxText(Objects.equals(medicine.getIsRx(), 1) ? "是" : "否");
        row.setStatusText(Objects.equals(medicine.getStatus(), 0) ? "停售" : "在售");
        row.setRemark(medicine.getRemark());
        return row;
    }

    private Medicine toMedicine(MedicineExcelRow row) {
        if (isBlank(row.getMedName())) {
            throw new IllegalArgumentException("药品名称不能为空");
        }
        if (isBlank(row.getMedType())) {
            throw new IllegalArgumentException("药品类型不能为空");
        }
        if (!List.of("中药", "西药", "器械").contains(row.getMedType().trim())) {
            throw new IllegalArgumentException("药品类型必须为中药、西药或器械");
        }
        if (row.getRetailPrice() == null || row.getRetailPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("零售价不能为空且不能小于0");
        }

        Medicine medicine = new Medicine();
        medicine.setMedId(row.getMedId());
        medicine.setMedName(row.getMedName().trim());
        medicine.setMedAlias(trimToNull(row.getMedAlias()));
        medicine.setMedType(row.getMedType().trim());
        medicine.setSpec(trimToNull(row.getSpec()));
        medicine.setUnit(trimToNull(row.getUnit()));
        medicine.setDosageForm(trimToNull(row.getDosageForm()));
        medicine.setManufacturer(trimToNull(row.getManufacturer()));
        medicine.setApprovalNo(trimToNull(row.getApprovalNo()));
        medicine.setRetailPrice(row.getRetailPrice());
        medicine.setPurchasePrice(row.getPurchasePrice());
        medicine.setIsRx(parseYesNo(row.getIsRxText()));
        medicine.setStatus(parseStatus(row.getStatusText()));
        medicine.setRemark(trimToNull(row.getRemark()));
        return medicine;
    }

    private Integer parseYesNo(String value) {
        if (isBlank(value)) {
            return 0;
        }
        String text = value.trim();
        if ("是".equals(text) || "1".equals(text)) {
            return 1;
        }
        if ("否".equals(text) || "0".equals(text)) {
            return 0;
        }
        throw new IllegalArgumentException("是否处方药只能填写是/否或1/0");
    }

    private Integer parseStatus(String value) {
        if (isBlank(value)) {
            return 1;
        }
        String text = value.trim();
        if ("在售".equals(text) || "正常".equals(text) || "1".equals(text)) {
            return 1;
        }
        if ("停售".equals(text) || "禁用".equals(text) || "0".equals(text)) {
            return 0;
        }
        throw new IllegalArgumentException("状态只能填写在售/停售或1/0");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
