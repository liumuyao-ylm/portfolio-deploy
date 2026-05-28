package com.pharmacy.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.entity.Supplier;
import com.pharmacy.entity.User;
import com.pharmacy.excel.PurchaseOrderExcelRow;
import com.pharmacy.service.PurchaseOrderService;
import com.pharmacy.service.SupplierService;
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
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<PurchaseOrder> create(@RequestBody PurchaseOrderCreateDTO dto) {
        PurchaseOrder order = purchaseOrderService.createPurchaseOrder(dto);
        return Result.success(order);
    }

    @PostMapping("/{id}/inbound")
    public Result<PurchaseOrder> inbound(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.confirmInbound(id);
        return Result.success(order);
    }

    @PostMapping
    public Result<PurchaseOrder> add(@RequestBody PurchaseOrder purchaseOrder) {
        boolean success = purchaseOrderService.save(purchaseOrder);
        return success ? Result.success(purchaseOrder) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            purchaseOrderService.deletePurchaseOrder(id);
        }
        return Result.success();
    }

    @PutMapping
    public Result<PurchaseOrder> update(@RequestBody PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseId() == null) {
            return Result.error("采购订单ID不能为空");
        }
        PurchaseOrder existing = purchaseOrderService.getById(purchaseOrder.getPurchaseId());
        if (existing == null) {
            return Result.error("未找到数据");
        }
        if (purchaseOrder.getPurchaseStatus() == null) {
            purchaseOrder.setPurchaseStatus(existing.getPurchaseStatus());
        }
        if (Integer.valueOf(1).equals(purchaseOrder.getPurchaseStatus())
                && !Integer.valueOf(1).equals(existing.getPurchaseStatus())) {
            purchaseOrder.setPurchaseStatus(existing.getPurchaseStatus());
            purchaseOrderService.updateById(purchaseOrder);
            return Result.success(purchaseOrderService.confirmInbound(purchaseOrder.getPurchaseId()));
        }
        if (Integer.valueOf(1).equals(existing.getPurchaseStatus())
                && !Integer.valueOf(1).equals(purchaseOrder.getPurchaseStatus())) {
            return Result.error("已入库采购单不能直接改回待入库");
        }
        boolean success = purchaseOrderService.updateById(purchaseOrder);
        return success ? Result.success(purchaseOrder) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<PurchaseOrder> getById(@PathVariable Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getById(id);
        fillDisplayNames(purchaseOrder);
        return purchaseOrder != null ? Result.success(purchaseOrder) : Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<PurchaseOrder>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        Page<PurchaseOrder> resultPage = purchaseOrderService.page(page, wrapper);
        resultPage.getRecords().forEach(this::fillDisplayNames);
        return Result.success(resultPage);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        List<PurchaseOrderExcelRow> rows = purchaseOrderService.list(wrapper)
                .stream()
                .peek(this::fillDisplayNames)
                .map(this::toExcelRow)
                .collect(Collectors.toList());

        ExcelResponseUtil.prepareExcelResponse(response, "采购订单数据.xlsx");
        EasyExcel.write(response.getOutputStream(), PurchaseOrderExcelRow.class)
                .sheet("采购订单")
                .doWrite(rows);
    }

    private PurchaseOrderExcelRow toExcelRow(PurchaseOrder order) {
        PurchaseOrderExcelRow row = new PurchaseOrderExcelRow();
        row.setPurchaseId(order.getPurchaseId());
        row.setSupplierName(order.getSupplierName());
        row.setUserRealName(order.getUserRealName());
        row.setPurchaseTime(order.getPurchaseTime() == null ? null : order.getPurchaseTime().toString());
        row.setTotalAmount(order.getTotalAmount());
        row.setPayType(order.getPayType());
        if (Objects.equals(order.getPurchaseStatus(), 1)) {
            row.setPurchaseStatusText("已入库");
        } else if (Objects.equals(order.getPurchaseStatus(), 2)) {
            row.setPurchaseStatusText("已作废");
        } else {
            row.setPurchaseStatusText("待入库");
        }
        row.setRemark(order.getRemark());
        return row;
    }

    private void fillDisplayNames(PurchaseOrder order) {
        if (order == null) {
            return;
        }
        if (order.getSupplierId() != null) {
            Supplier supplier = supplierService.getById(order.getSupplierId());
            if (supplier != null) {
                order.setSupplierName(supplier.getSupplierName());
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
