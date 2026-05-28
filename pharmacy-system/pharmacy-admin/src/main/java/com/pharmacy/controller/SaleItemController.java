package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.service.MedicineService;
import com.pharmacy.service.SaleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale-item")
public class SaleItemController {

    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private MedicineService medicineService;

    @PostMapping
    public Result<SaleItem> add(@RequestBody SaleItem saleItem) {
        boolean success = saleItemService.save(saleItem);
        return success ? Result.success(saleItem) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = saleItemService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<SaleItem> update(@RequestBody SaleItem saleItem) {
        boolean success = saleItemService.updateById(saleItem);
        return success ? Result.success(saleItem) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<SaleItem> getById(@PathVariable Long id) {
        SaleItem saleItem = saleItemService.getById(id);
        fillDisplayNames(saleItem);
        return saleItem != null ? Result.success(saleItem) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<SaleItem>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long orderId) {
        Page<SaleItem> page = new Page<>(current, size);
        LambdaQueryWrapper<SaleItem> wrapper = new LambdaQueryWrapper<>();
        if (orderId != null) {
            wrapper.eq(SaleItem::getOrderId, orderId);
        }
        wrapper.orderByDesc(SaleItem::getCreateTime);
        Page<SaleItem> resultPage = saleItemService.page(page, wrapper);
        resultPage.getRecords().forEach(this::fillDisplayNames);
        return Result.success(resultPage);
    }

    private void fillDisplayNames(SaleItem item) {
        if (item == null) {
            return;
        }
        if (item.getMedId() != null) {
            Medicine medicine = medicineService.getById(item.getMedId());
            if (medicine != null) {
                item.setMedName(medicine.getMedName());
            }
        }
    }
}
