package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Customer;
import com.pharmacy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public Result<Customer> add(@RequestBody Customer customer) {
        boolean success = customerService.save(customer);
        return success ? Result.success(customer) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = customerService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<Customer> update(@RequestBody Customer customer) {
        boolean success = customerService.updateById(customer);
        return success ? Result.success(customer) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Integer id) {
        Customer customer = customerService.getById(id);
        return customer != null ? Result.success(customer) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<Customer>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String custName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer isMember) {
        Page<Customer> page = new Page<>(current, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (custName != null && !custName.isEmpty()) {
            wrapper.like(Customer::getCustName, custName);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(Customer::getPhone, phone);
        }
        if (isMember != null) {
            wrapper.eq(Customer::getIsMember, isMember);
        }
        wrapper.orderByAsc(Customer::getCustId);
        return Result.success(customerService.page(page, wrapper));
    }
}
