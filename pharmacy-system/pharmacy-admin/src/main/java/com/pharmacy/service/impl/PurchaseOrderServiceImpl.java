package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.dto.PurchaseItemDTO;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.entity.PurchaseOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.exception.BusinessException;
import com.pharmacy.mapper.PurchaseOrderMapper;
import com.pharmacy.service.PurchaseItemService;
import com.pharmacy.service.PurchaseOrderService;
import com.pharmacy.service.StockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private PurchaseItemService purchaseItemService;

    @Autowired
    private StockService stockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder createPurchaseOrder(PurchaseOrderCreateDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("采购明细不能为空");
        }
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        BeanUtils.copyProperties(dto, purchaseOrder);
        purchaseOrder.setPurchaseStatus(0);
        purchaseOrder.setCreateTime(LocalDateTime.now());
        purchaseOrder.setUpdateTime(LocalDateTime.now());
        
        save(purchaseOrder);
        
        Long purchaseId = purchaseOrder.getPurchaseId();
        List<PurchaseItem> items = new ArrayList<>();
        for (PurchaseItemDTO itemDTO : dto.getItems()) {
            PurchaseItem item = new PurchaseItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setPurchaseId(purchaseId);
            item.setCreateTime(LocalDateTime.now());
            if (item.getBatchNo() == null || item.getBatchNo().trim().isEmpty()) {
                item.setBatchNo("DEFAULT");
            } else {
                item.setBatchNo(item.getBatchNo().trim());
            }
            items.add(item);
        }
        purchaseItemService.saveBatch(items);
        
        return purchaseOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder confirmInbound(Long purchaseId) {
        PurchaseOrder order = getById(purchaseId);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        if (Objects.equals(order.getPurchaseStatus(), 1)) {
            throw new BusinessException("采购订单已入库，请勿重复操作");
        }
        if (Objects.equals(order.getPurchaseStatus(), 2)) {
            throw new BusinessException("采购订单已作废，不能入库");
        }

        LambdaQueryWrapper<PurchaseItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PurchaseItem::getPurchaseId, purchaseId);
        List<PurchaseItem> items = purchaseItemService.list(itemWrapper);
        if (items.isEmpty()) {
            throw new BusinessException("采购订单没有明细，不能入库");
        }

        for (PurchaseItem item : items) {
            increaseStock(order, item);
        }

        order.setPurchaseStatus(1);
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseOrder(Long purchaseId) {
        PurchaseOrder order = getById(purchaseId);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        if (Objects.equals(order.getPurchaseStatus(), 1)) {
            rollbackPurchaseStock(purchaseId);
        }
        LambdaQueryWrapper<PurchaseItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PurchaseItem::getPurchaseId, purchaseId);
        purchaseItemService.remove(itemWrapper);
        removeById(purchaseId);
    }

    private void increaseStock(PurchaseOrder order, PurchaseItem item) {
        if (item.getMedId() == null) {
            throw new BusinessException("采购明细药品不能为空");
        }
        if (item.getPurchaseNum() == null || item.getPurchaseNum() <= 0) {
            throw new BusinessException("采购数量必须大于0");
        }

        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getMedId, item.getMedId())
                .eq(Stock::getBatchNo, item.getBatchNo())
                .eq(Stock::getStatus, 1)
                .last("LIMIT 1 FOR UPDATE");

        Stock stock = stockService.getOne(wrapper, false);
        if (stock == null) {
            stock = new Stock();
            stock.setMedId(item.getMedId());
            stock.setBatchNo(item.getBatchNo());
            stock.setProductionDate(item.getProductionDate());
            stock.setExpireDate(item.getExpireDate());
            stock.setStockNum(item.getPurchaseNum());
            stock.setStockMin(0);
            stock.setPurchasePrice(item.getPurchasePrice());
            stock.setCabinet(item.getCabinet());
            stock.setSupplierId(order.getSupplierId());
            stock.setPurchaseId(order.getPurchaseId());
            stock.setPurchaseItemId(item.getItemId());
            stock.setStatus(1);
            stock.setRemark(item.getRemark());
            stockService.save(stock);
            return;
        }

        int currentStock = stock.getStockNum() == null ? 0 : stock.getStockNum();
        int purchaseNum = item.getPurchaseNum() == null ? 0 : item.getPurchaseNum();
        stock.setStockNum(currentStock + purchaseNum);
        stock.setPurchasePrice(item.getPurchasePrice());
        stock.setCabinet(item.getCabinet());
        stock.setProductionDate(item.getProductionDate());
        stock.setExpireDate(item.getExpireDate());
        stock.setSupplierId(order.getSupplierId());
        stockService.updateById(stock);
    }

    private void rollbackPurchaseStock(Long purchaseId) {
        LambdaQueryWrapper<PurchaseItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PurchaseItem::getPurchaseId, purchaseId);
        List<PurchaseItem> items = purchaseItemService.list(itemWrapper);

        for (PurchaseItem item : items) {
            LambdaQueryWrapper<Stock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(Stock::getMedId, item.getMedId())
                    .eq(Stock::getBatchNo, item.getBatchNo())
                    .eq(Stock::getStatus, 1)
                    .last("LIMIT 1 FOR UPDATE");
            Stock stock = stockService.getOne(stockWrapper, false);
            int purchaseNum = item.getPurchaseNum() == null ? 0 : item.getPurchaseNum();
            if (stock == null) {
                throw new BusinessException("采购单对应库存不存在，不能直接删除");
            }
            int stockNum = stock.getStockNum() == null ? 0 : stock.getStockNum();
            if (stockNum < purchaseNum) {
                throw new BusinessException("采购单已有库存被销售或调整，不能直接删除");
            }
            stock.setStockNum(stockNum - purchaseNum);
            stockService.updateById(stock);
        }
    }
}
