package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.annotation.OperateLog;
import com.pharmacy.common.Result;
import com.pharmacy.dto.StockCheckCreateDTO;
import com.pharmacy.entity.StockCheck;
import com.pharmacy.service.StockCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-check")
public class StockCheckController {

    @Autowired
    private StockCheckService stockCheckService;

    @PostMapping
    @OperateLog(module = "库存管理", type = "新增", content = "新增盘点记录")
    public Result<StockCheck> add(@RequestBody StockCheck stockCheck) {
        boolean success = stockCheckService.save(stockCheck);
        return success ? Result.success(stockCheck) : Result.error("添加失败");
    }

    @PostMapping("/create")
    @OperateLog(module = "库存管理", type = "新增", content = "创建盘点单")
    @Transactional(rollbackFor = Exception.class)
    public Result<List<StockCheck>> create(@RequestBody Map<String, Object> data) {
        List<StockCheck> stockCheckList = new ArrayList<>();
        
        String checkNo = (String) data.get("checkNo");
        Integer checkUser = data.get("checkUser") instanceof Integer ? (Integer) data.get("checkUser") : 1;
        String checkTimeStr = (String) data.get("checkTime");
        String remark = (String) data.get("remark");
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        
        LocalDateTime checkTime = null;
        if (checkTimeStr != null) {
            checkTime = LocalDateTime.parse(checkTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> item : items) {
                StockCheck stockCheck = new StockCheck();
                stockCheck.setCheckNo(checkNo);
                stockCheck.setCheckUser(checkUser);
                stockCheck.setCheckTime(checkTime);
                stockCheck.setRemark(remark);
                
                Integer medId = item.get("medId") instanceof Integer ? (Integer) item.get("medId") : null;
                String batchNo = (String) item.get("batchNo");
                Integer systemStock = item.get("systemStock") instanceof Integer ? (Integer) item.get("systemStock") : 0;
                Integer actualStock = item.get("actualStock") instanceof Integer ? (Integer) item.get("actualStock") : 0;
                Object unitPriceObj = item.get("unitPrice");
                BigDecimal unitPrice = null;
                if (unitPriceObj instanceof BigDecimal) {
                    unitPrice = (BigDecimal) unitPriceObj;
                } else if (unitPriceObj instanceof Number) {
                    unitPrice = BigDecimal.valueOf(((Number) unitPriceObj).doubleValue());
                }
                String reason = (String) item.get("reason");
                String itemRemark = (String) item.get("remark");
                
                stockCheck.setMedId(medId);
                stockCheck.setBatchNo(batchNo);
                stockCheck.setSystemStock(systemStock);
                stockCheck.setActualStock(actualStock);
                stockCheck.setUnitPrice(unitPrice);
                stockCheck.setReason(reason);
                stockCheck.setRemark(itemRemark);
                
                Integer profitLoss = actualStock - systemStock;
                stockCheck.setProfitLoss(profitLoss);
                
                if (unitPrice != null) {
                    BigDecimal profitLossAmount = unitPrice.multiply(new BigDecimal(profitLoss));
                    stockCheck.setProfitLossAmount(profitLossAmount);
                }
                
                stockCheckList.add(stockCheck);
            }
        }
        
        if (!stockCheckList.isEmpty()) {
            boolean success = stockCheckService.saveBatch(stockCheckList);
            return success ? Result.success(stockCheckList) : Result.error("创建失败");
        } else {
            return Result.error("请至少添加一条盘点明细");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = stockCheckService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<StockCheck> update(@RequestBody StockCheck stockCheck) {
        boolean success = stockCheckService.updateById(stockCheck);
        return success ? Result.success(stockCheck) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<StockCheck> getById(@PathVariable Long id) {
        StockCheck stockCheck = stockCheckService.getById(id);
        return stockCheck != null ? Result.success(stockCheck) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<StockCheck>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String checkNo) {
        Page<StockCheck> page = new Page<>(current, size);
        LambdaQueryWrapper<StockCheck> wrapper = new LambdaQueryWrapper<>();
        if (checkNo != null && !checkNo.isEmpty()) {
            wrapper.eq(StockCheck::getCheckNo, checkNo);
        }
        wrapper.orderByDesc(StockCheck::getCreateTime);
        return Result.success(stockCheckService.page(page, wrapper));
    }
}
