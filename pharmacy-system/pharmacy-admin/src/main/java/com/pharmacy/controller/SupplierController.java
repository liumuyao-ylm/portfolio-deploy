package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Supplier;
import com.pharmacy.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public Result<Supplier> add(@RequestBody Supplier supplier) {
        boolean success = supplierService.save(supplier);
        return success ? Result.success(supplier) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = supplierService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<Supplier> update(@RequestBody Supplier supplier) {
        boolean success = supplierService.updateById(supplier);
        return success ? Result.success(supplier) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Supplier> getById(@PathVariable Integer id) {
        Supplier supplier = supplierService.getById(id);
        return supplier != null ? Result.success(supplier) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<Supplier>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String contact) {
        Page<Supplier> page = new Page<>(current, size);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (supplierName != null && !supplierName.isEmpty()) {
            wrapper.like(Supplier::getSupplierName, supplierName);
        }
        if (contact != null && !contact.isEmpty()) {
            wrapper.like(Supplier::getContact, contact);
        }
        wrapper.orderByAsc(Supplier::getSupplierId);
        return Result.success(supplierService.page(page, wrapper));
    }
}
