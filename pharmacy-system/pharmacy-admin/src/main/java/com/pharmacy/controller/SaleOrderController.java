package com.pharmacy.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.entity.User;
import com.pharmacy.excel.SaleOrderExcelRow;
import com.pharmacy.service.CustomerService;
import com.pharmacy.service.SaleOrderService;
import com.pharmacy.service.UserService;
import com.pharmacy.util.ExcelResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sale-order")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<SaleOrder> create(@RequestBody SaleOrderCreateDTO dto) {
        SaleOrder order = saleOrderService.createSaleOrder(dto);
        return Result.success(order);
    }

    @PostMapping("/{id}/outbound")
    public Result<SaleOrder> outbound(@PathVariable Long id) {
        SaleOrder order = saleOrderService.confirmOutbound(id);
        return Result.success(order);
    }

    @PostMapping
    public Result<SaleOrder> add(@RequestBody SaleOrder saleOrder) {
        boolean success = saleOrderService.save(saleOrder);
        return success ? Result.success(saleOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        saleOrderService.deleteSaleOrder(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            saleOrderService.deleteSaleOrder(id);
        }
        return Result.success();
    }

    @PutMapping
    public Result<SaleOrder> update(@RequestBody SaleOrder saleOrder) {
        if (saleOrder.getOrderId() == null) {
            return Result.error("销售订单ID不能为空");
        }
        SaleOrder existing = saleOrderService.getById(saleOrder.getOrderId());
        if (existing == null) {
            return Result.error("未找到数据");
        }
        if (saleOrder.getPayStatus() == null) {
            saleOrder.setPayStatus(existing.getPayStatus());
        }
        if (Integer.valueOf(1).equals(saleOrder.getPayStatus())
                && !Integer.valueOf(1).equals(existing.getPayStatus())) {
            saleOrder.setPayStatus(existing.getPayStatus());
            saleOrderService.updateById(saleOrder);
            return Result.success(saleOrderService.confirmOutbound(saleOrder.getOrderId()));
        }
        if (Integer.valueOf(1).equals(existing.getPayStatus())
                && !Integer.valueOf(1).equals(saleOrder.getPayStatus())) {
            return Result.error("已出库销售单不能直接改回待支付");
        }
        boolean success = saleOrderService.updateById(saleOrder);
        return success ? Result.success(saleOrder) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<SaleOrder> getById(@PathVariable Long id) {
        SaleOrder saleOrder = saleOrderService.getById(id);
        fillDisplayNames(saleOrder);
        return saleOrder != null ? Result.success(saleOrder) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<SaleOrder>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<SaleOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SaleOrder::getCreateTime);
        Page<SaleOrder> resultPage = saleOrderService.page(page, wrapper);
        resultPage.getRecords().forEach(this::fillDisplayNames);
        return Result.success(resultPage);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SaleOrder::getCreateTime);
        List<SaleOrderExcelRow> rows = saleOrderService.list(wrapper)
                .stream()
                .peek(this::fillDisplayNames)
                .map(this::toExcelRow)
                .collect(Collectors.toList());

        ExcelResponseUtil.prepareExcelResponse(response, "销售订单数据.xlsx");
        EasyExcel.write(response.getOutputStream(), SaleOrderExcelRow.class)
                .sheet("销售订单")
                .doWrite(rows);
    }

    private SaleOrderExcelRow toExcelRow(SaleOrder order) {
        SaleOrderExcelRow row = new SaleOrderExcelRow();
        row.setOrderId(order.getOrderId());
        row.setCustName(order.getCustName());
        row.setUserRealName(order.getUserRealName());
        row.setCreateTime(order.getCreateTime() == null ? null : order.getCreateTime().toString());
        row.setTotalPrice(order.getTotalPrice());
        row.setPayType(order.getPayType());
        row.setOrderTypeText(Objects.equals(order.getOrderType(), 0) ? "线上" : "线下");
        if (Objects.equals(order.getPayStatus(), 1)) {
            row.setPayStatusText("已支付");
        } else if (Objects.equals(order.getPayStatus(), 2)) {
            row.setPayStatusText("已退款");
        } else {
            row.setPayStatusText("未支付");
        }
        row.setRemark(order.getRemark());
        return row;
    }

    private void fillDisplayNames(SaleOrder order) {
        if (order == null) {
            return;
        }
        if (order.getCustId() != null) {
            Customer customer = customerService.getById(order.getCustId());
            if (customer != null) {
                order.setCustName(customer.getCustName());
            }
        }
        if (order.getUserId() != null) {
            User user = userService.getById(order.getUserId());
            if (user != null) {
                order.setUserRealName(user.getRealName());
            }
        }
    }
}
