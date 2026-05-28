package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.SysLog;
import com.pharmacy.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys-log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/{id}")
    public Result<SysLog> getById(@PathVariable Long id) {
        SysLog sysLog = sysLogService.getById(id);
        return sysLog != null ? Result.success(sysLog) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<SysLog>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String operModule,
            @RequestParam(required = false) String operType) {
        Page<SysLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (operModule != null && !operModule.isEmpty()) {
            wrapper.eq(SysLog::getOperModule, operModule);
        }
        if (operType != null && !operType.isEmpty()) {
            wrapper.eq(SysLog::getOperType, operType);
        }
        wrapper.orderByDesc(SysLog::getOperTime);
        return Result.success(sysLogService.page(page, wrapper));
    }
}
