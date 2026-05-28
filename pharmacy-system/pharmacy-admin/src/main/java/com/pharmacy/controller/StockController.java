package com.pharmacy.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Stock;
import com.pharmacy.excel.StockExcelRow;
import com.pharmacy.service.StockService;
import com.pharmacy.util.ExcelResponseUtil;
import com.pharmacy.vo.StockVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping
    public Result<Stock> add(@RequestBody Stock stock) {
        boolean success = stockService.save(stock);
        return success ? Result.success(stock) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = stockService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<Stock> update(@RequestBody Stock stock) {
        boolean success = stockService.updateById(stock);
        return success ? Result.success(stock) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Stock> getById(@PathVariable Integer id) {
        Stock stock = stockService.getById(id);
        return stock != null ? Result.success(stock) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<IPage<Stock>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Stock> page = new Page<>(current, size);
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Stock::getCreateTime);
        return Result.success(stockService.page(page, wrapper));
    }
    
    @GetMapping("/list/with-medicine")
    public Result<IPage<StockVO>> listWithMedicine(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<StockVO> page = new Page<>(current, size);
        return Result.success(stockService.getStockPageWithMedicine(page));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<StockExcelRow> rows = stockService.listStockWithMedicine()
                .stream()
                .map(this::toExcelRow)
                .collect(Collectors.toList());

        ExcelResponseUtil.prepareExcelResponse(response, "库存数据.xlsx");
        EasyExcel.write(response.getOutputStream(), StockExcelRow.class)
                .sheet("库存数据")
                .doWrite(rows);
    }

    private StockExcelRow toExcelRow(StockVO stock) {
        StockExcelRow row = new StockExcelRow();
        row.setStockId(stock.getStockId());
        row.setMedId(stock.getMedId());
        row.setMedName(stock.getMedName());
        row.setSpec(stock.getSpec());
        row.setUnit(stock.getUnit());
        row.setSupplierName(stock.getSupplierName());
        row.setBatchNo(stock.getBatchNo());
        row.setStockNum(stock.getStockNum());
        row.setStockMin(stock.getStockMin());
        row.setPurchasePrice(stock.getPurchasePrice());
        row.setProductionDate(stock.getProductionDate() == null ? null : stock.getProductionDate().toString());
        row.setExpireDate(stock.getExpireDate() == null ? null : stock.getExpireDate().toString());
        row.setCabinet(stock.getCabinet());
        row.setStatusText(Objects.equals(stock.getStatus(), 1) ? "正常" : "禁用");
        row.setRemark(stock.getRemark());
        return row;
    }
}
