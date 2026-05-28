package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.service.MedicineService;
import com.pharmacy.service.PurchaseItemService;
import com.pharmacy.service.PurchaseOrderService;
import com.pharmacy.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-item")
public class PurchaseItemController {

    @Autowired
    private PurchaseItemService purchaseItemService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private StockService stockService;

    @PostMapping
    public Result<PurchaseItem> add(@RequestBody PurchaseItem purchaseItem) {
        Result<Void> validation = validatePurchaseItem(purchaseItem);
        if (validation.getCode() != 200) {
            return Result.error(validation.getCode(), validation.getMessage());
        }
        fillTotalPrice(purchaseItem);
        boolean success = purchaseItemService.save(purchaseItem);
        return success ? Result.success(purchaseItem) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        PurchaseItem purchaseItem = purchaseItemService.getById(id);
        if (purchaseItem == null) {
            return Result.error(400, "采购明细不存在");
        }
        Result<Void> editable = validatePurchaseOrderEditable(purchaseItem.getPurchaseId());
        if (editable.getCode() != 200) {
            return Result.error(editable.getCode(), editable.getMessage());
        }
        boolean success = purchaseItemService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<PurchaseItem> update(@RequestBody PurchaseItem purchaseItem) {
        Result<Void> validation = validatePurchaseItem(purchaseItem);
        if (validation.getCode() != 200) {
            return Result.error(validation.getCode(), validation.getMessage());
        }
        fillTotalPrice(purchaseItem);
        boolean success = purchaseItemService.updateById(purchaseItem);
        return success ? Result.success(purchaseItem) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<PurchaseItem> getById(@PathVariable Long id) {
        PurchaseItem purchaseItem = purchaseItemService.getById(id);
        fillDisplayNames(purchaseItem);
        return purchaseItem != null ? Result.success(purchaseItem) : Result.error("未找到数据");
    }

    @PostMapping("/stock-in/{purchaseId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> stockIn(@PathVariable Long purchaseId) {
        PurchaseOrder order = purchaseOrderService.getById(purchaseId);
        if (order == null) {
            return Result.error(400, "采购订单不存在");
        }
        if (Integer.valueOf(1).equals(order.getPurchaseStatus())) {
            return Result.error(400, "该采购订单已入库，请勿重复操作");
        }
        if (Integer.valueOf(2).equals(order.getPurchaseStatus())) {
            return Result.error(400, "已作废的采购订单不能入库");
        }

        LambdaQueryWrapper<PurchaseItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PurchaseItem::getPurchaseId, purchaseId);
        List<PurchaseItem> items = purchaseItemService.list(itemWrapper);
        if (items.isEmpty()) {
            return Result.error(400, "采购订单没有明细，无法入库");
        }

        for (PurchaseItem item : items) {
            stockInItem(order, item);
        }

        PurchaseOrder updateOrder = new PurchaseOrder();
        updateOrder.setPurchaseId(purchaseId);
        updateOrder.setPurchaseStatus(1);
        purchaseOrderService.updateById(updateOrder);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Page<PurchaseItem>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long purchaseId) {
        Page<PurchaseItem> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseItem> wrapper = new LambdaQueryWrapper<>();
        if (purchaseId != null) {
            wrapper.eq(PurchaseItem::getPurchaseId, purchaseId);
        }
        wrapper.orderByDesc(PurchaseItem::getCreateTime);
        Page<PurchaseItem> resultPage = purchaseItemService.page(page, wrapper);
        resultPage.getRecords().forEach(this::fillDisplayNames);
        return Result.success(resultPage);
    }

    private void stockInItem(PurchaseOrder order, PurchaseItem item) {
        LambdaQueryWrapper<Stock> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(Stock::getMedId, item.getMedId())
                .eq(Stock::getBatchNo, item.getBatchNo());
        if (order.getSupplierId() != null) {
            stockWrapper.eq(Stock::getSupplierId, order.getSupplierId());
        }
        Stock stock = stockService.getOne(stockWrapper, false);

        if (stock == null) {
            stock = new Stock();
            stock.setMedId(item.getMedId());
            stock.setBatchNo(item.getBatchNo());
            stock.setExpireDate(item.getExpireDate());
            stock.setStockNum(item.getPurchaseNum());
            stock.setPurchasePrice(item.getPurchasePrice());
            stock.setCabinet(item.getCabinet());
            stock.setProductionDate(item.getProductionDate());
            stock.setSupplierId(order.getSupplierId());
            stock.setStatus(1);
            stock.setRemark("采购单 " + order.getPurchaseId() + " 入库");
            stockService.save(stock);
            return;
        }

        stock.setStockNum((stock.getStockNum() == null ? 0 : stock.getStockNum()) + item.getPurchaseNum());
        stock.setExpireDate(item.getExpireDate());
        stock.setPurchasePrice(item.getPurchasePrice());
        stock.setCabinet(item.getCabinet());
        stock.setProductionDate(item.getProductionDate());
        stock.setStatus(1);
        stockService.updateById(stock);
    }

    private void fillDisplayNames(PurchaseItem item) {
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

    private Result<Void> validatePurchaseItem(PurchaseItem purchaseItem) {
        if (purchaseItem.getPurchaseId() == null) {
            return Result.error(400, "采购订单ID不能为空");
        }
        Result<Void> editable = validatePurchaseOrderEditable(purchaseItem.getPurchaseId());
        if (editable.getCode() != 200) {
            return editable;
        }
        if (purchaseItem.getMedId() == null) {
            return Result.error(400, "药品ID不能为空");
        }
        if (medicineService.getById(purchaseItem.getMedId()) == null) {
            return Result.error(400, "药品不存在，请先维护药品信息");
        }
        if (purchaseItem.getBatchNo() == null || purchaseItem.getBatchNo().trim().isEmpty()) {
            return Result.error(400, "批号不能为空");
        }
        if (purchaseItem.getPurchaseNum() == null || purchaseItem.getPurchaseNum() <= 0) {
            return Result.error(400, "采购数量必须大于0");
        }
        if (purchaseItem.getPurchasePrice() == null || purchaseItem.getPurchasePrice().compareTo(BigDecimal.ZERO) < 0) {
            return Result.error(400, "采购单价不能小于0");
        }
        if (purchaseItem.getExpireDate() == null) {
            return Result.error(400, "有效期不能为空");
        }
        return Result.success();
    }

    private Result<Void> validatePurchaseOrderEditable(Long purchaseId) {
        PurchaseOrder order = purchaseOrderService.getById(purchaseId);
        if (order == null) {
            return Result.error(400, "采购订单不存在，请先创建采购订单");
        }
        if (Integer.valueOf(1).equals(order.getPurchaseStatus())) {
            return Result.error(400, "采购订单已入库，不能修改明细");
        }
        if (Integer.valueOf(2).equals(order.getPurchaseStatus())) {
            return Result.error(400, "采购订单已作废，不能修改明细");
        }
        return Result.success();
    }

    private void fillTotalPrice(PurchaseItem purchaseItem) {
        purchaseItem.setTotalPrice(
                purchaseItem.getPurchasePrice()
                        .multiply(BigDecimal.valueOf(purchaseItem.getPurchaseNum()))
        );
    }
}
