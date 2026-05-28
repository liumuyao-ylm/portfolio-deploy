package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pharmacy.common.Result;
import com.pharmacy.entity.*;
import com.pharmacy.mapper.StockMapper;
import com.pharmacy.service.*;
import com.pharmacy.vo.StockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SaleOrderService saleOrderService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private StockService stockService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private StockMapper stockMapper;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> data = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        // 当日营业额
        LambdaQueryWrapper<SaleOrder> saleWrapper = new LambdaQueryWrapper<>();
        saleWrapper.between(SaleOrder::getCreateTime, startOfDay, endOfDay);
        List<SaleOrder> todayOrders = saleOrderService.list(saleWrapper);
        BigDecimal todayTurnover = todayOrders.stream()
                .map(SaleOrder::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("todayTurnover", todayTurnover);

        // 订单总数
        long orderCount = saleOrderService.count() + purchaseOrderService.count();
        data.put("orderCount", orderCount);

        // 库存总数
        long stockCount = stockService.count();
        data.put("stockCount", stockCount);

        // 过期数量和近效期预警数量（今天至未来30天）
        List<Stock> allStocks = stockService.list();
        long expiredCount = 0;
        long nearExpireCount = 0;
        LocalDate warningDate = today.plusDays(30);
        for (Stock stock : allStocks) {
            if (stock.getExpireDate() == null) {
                continue;
            }
            if (stock.getExpireDate().isBefore(today)) {
                expiredCount++;
            } else if (!stock.getExpireDate().isAfter(warningDate)) {
                nearExpireCount++;
            }
        }
        data.put("expiredCount", expiredCount);
        data.put("nearExpireCount", nearExpireCount);

        return Result.success(data);
    }

    @GetMapping("/recent-orders")
    public Result<List<SaleOrder>> getRecentOrders() {
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SaleOrder::getCreateTime);
        wrapper.last("LIMIT 5");
        List<SaleOrder> orders = saleOrderService.list(wrapper);
        return Result.success(orders);
    }

    @GetMapping("/expired-stocks")
    public Result<List<StockVO>> getExpiredStocks() {
        List<StockVO> stocks = stockMapper.selectExpiredStockReminders(LocalDate.now());
        return Result.success(stocks);
    }

    @GetMapping("/recent-logs")
    public Result<List<SysLog>> getRecentLogs() {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysLog::getOperTime);
        wrapper.last("LIMIT 5");
        List<SysLog> logs = sysLogService.list(wrapper);
        return Result.success(logs);
    }
}
