package com.pharmacy.controller;

import com.pharmacy.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "欢迎使用乡镇药店管理系统");
        data.put("version", "1.0.0");
        return Result.success(data);
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统运行正常");
    }
}
