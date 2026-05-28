package com.pharmacy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.mapper.MedicineMapper;
import com.pharmacy.vo.ClientMedicineVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/client/medicines")
public class ClientMedicineController {

    @Autowired
    private MedicineMapper medicineMapper;

    @GetMapping
    public Result<Page<ClientMedicineVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dosageForm,
            @RequestParam(required = false) Integer isRx,
            @RequestParam(required = false) String stockStatus) {
        log.info("Client medicine query - current: {}, size: {}, keyword: {}, type: {}, dosageForm: {}, isRx: {}, stockStatus: {}",
                current, size, keyword, type, dosageForm, isRx, stockStatus);
        Page<ClientMedicineVO> page = new Page<>(current, size);
        Page<ClientMedicineVO> result = medicineMapper.selectClientMedicinePage(page, keyword, type, dosageForm, isRx, stockStatus);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ClientMedicineVO> detail(@PathVariable Integer id) {
        ClientMedicineVO medicine = medicineMapper.selectClientMedicineById(id);
        return medicine != null ? Result.success(medicine) : Result.error(404, "未找到药品信息");
    }
}
