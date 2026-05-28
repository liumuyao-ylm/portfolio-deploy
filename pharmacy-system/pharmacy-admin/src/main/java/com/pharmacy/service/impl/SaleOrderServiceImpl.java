package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.dto.SaleItemDTO;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.entity.Stock;
import com.pharmacy.exception.BusinessException;
import com.pharmacy.mapper.SaleOrderMapper;
import com.pharmacy.service.CustomerService;
import com.pharmacy.service.MedicineService;
import com.pharmacy.service.SaleItemService;
import com.pharmacy.service.SaleOrderService;
import com.pharmacy.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StockService stockService;

    @Autowired
    private MedicineService medicineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder createSaleOrder(SaleOrderCreateDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("销售明细不能为空");
        }

        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setCustId(dto.getCustId());
        saleOrder.setUserId(dto.getUserId());
        saleOrder.setTotalPrice(dto.getTotalPrice());
        saleOrder.setPayType(dto.getPayType());
        saleOrder.setOrderType(dto.getOrderType());
        saleOrder.setPayStatus(0);
        saleOrder.setRemark(dto.getRemark());
        saleOrder.setCreateTime(LocalDateTime.now());
        saleOrder.setUpdateTime(LocalDateTime.now());
        save(saleOrder);

        Long orderId = saleOrder.getOrderId();
        List<SaleItem> items = new ArrayList<>();
        for (SaleItemDTO itemDTO : dto.getItems()) {
            items.add(buildSaleItem(itemDTO, orderId));
        }
        saleItemService.saveBatch(items);

        if (Objects.equals(dto.getPayStatus(), 1)) {
            return confirmOutbound(orderId);
        }
        return saleOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrder confirmOutbound(Long orderId) {
        SaleOrder saleOrder = getById(orderId);
        if (saleOrder == null) {
            throw new BusinessException("销售订单不存在");
        }
        if (Objects.equals(saleOrder.getPayStatus(), 1)) {
            throw new BusinessException("销售订单已出库，请勿重复操作");
        }
        if (Objects.equals(saleOrder.getPayStatus(), 2)) {
            throw new BusinessException("销售订单已退款，不能出库");
        }

        LambdaQueryWrapper<SaleItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SaleItem::getOrderId, orderId);
        List<SaleItem> originalItems = saleItemService.list(itemWrapper);
        if (originalItems.isEmpty()) {
            throw new BusinessException("销售订单没有明细，不能出库");
        }

        List<SaleItem> outboundItems = new ArrayList<>();
        for (SaleItem item : originalItems) {
            outboundItems.addAll(deductStockAndBuildItems(item, orderId));
        }
        saleItemService.remove(itemWrapper);
        saleItemService.saveBatch(outboundItems);

        saleOrder.setPayStatus(1);
        saleOrder.setUpdateTime(LocalDateTime.now());
        updateById(saleOrder);

        addCustomerConsume(saleOrder);
        return saleOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSaleOrder(Long orderId) {
        SaleOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }
        if (Objects.equals(order.getPayStatus(), 1)) {
            restoreSaleOrderEffects(orderId);
        } else {
            LambdaQueryWrapper<SaleItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(SaleItem::getOrderId, orderId);
            saleItemService.remove(itemWrapper);
        }
        removeById(orderId);
    }

    private SaleItem buildSaleItem(SaleItemDTO itemDTO, Long orderId) {
        if (itemDTO.getMedId() == null) {
            throw new BusinessException("药品不能为空");
        }
        if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
            throw new BusinessException("销售数量必须大于0");
        }

        SaleItem item = new SaleItem();
        item.setOrderId(orderId);
        item.setMedId(itemDTO.getMedId());
        item.setBatchNo(itemDTO.getBatchNo() == null ? "" : itemDTO.getBatchNo().trim());
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getUnitPrice());
        item.setTotalPrice(calculateTotalPrice(itemDTO.getUnitPrice(), itemDTO.getQuantity()));
        item.setCreateTime(LocalDateTime.now());
        return item;
    }

    private List<SaleItem> deductStockAndBuildItems(SaleItem sourceItem, Long orderId) {
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getMedId, sourceItem.getMedId())
                .eq(Stock::getStatus, 1);
        if (sourceItem.getBatchNo() != null && !sourceItem.getBatchNo().trim().isEmpty()) {
            wrapper.eq(Stock::getBatchNo, sourceItem.getBatchNo().trim());
        }
        wrapper.orderByAsc(Stock::getExpireDate)
                .orderByAsc(Stock::getCreateTime)
                .orderByAsc(Stock::getStockId)
                .last("FOR UPDATE");

        List<Stock> stocks = stockService.list(wrapper);
        int available = stocks.stream()
                .map(Stock::getStockNum)
                .filter(num -> num != null)
                .mapToInt(Integer::intValue)
                .sum();

        if (available < sourceItem.getQuantity()) {
            String batchText = sourceItem.getBatchNo() == null || sourceItem.getBatchNo().trim().isEmpty()
                    ? ""
                    : "，批号：" + sourceItem.getBatchNo().trim();
            throw new BusinessException("库存不足：" + getMedicineName(sourceItem.getMedId())
                    + batchText + "，当前库存" + available + "，本次销售" + sourceItem.getQuantity());
        }

        List<SaleItem> saleItems = new ArrayList<>();
        int remaining = sourceItem.getQuantity();
        for (Stock stock : stocks) {
            if (remaining <= 0) {
                break;
            }
            int stockNum = stock.getStockNum() == null ? 0 : stock.getStockNum();
            int deductNum = Math.min(stockNum, remaining);
            if (deductNum <= 0) {
                continue;
            }

            stock.setStockNum(stockNum - deductNum);
            stockService.updateById(stock);

            SaleItem item = new SaleItem();
            item.setOrderId(orderId);
            item.setMedId(sourceItem.getMedId());
            item.setBatchNo(stock.getBatchNo());
            item.setQuantity(deductNum);
            item.setUnitPrice(sourceItem.getUnitPrice());
            item.setTotalPrice(calculateTotalPrice(sourceItem.getUnitPrice(), deductNum));
            item.setCreateTime(LocalDateTime.now());
            saleItems.add(item);

            remaining -= deductNum;
        }
        return saleItems;
    }

    private void restoreSaleOrderEffects(Long orderId) {
        SaleOrder order = getById(orderId);
        LambdaQueryWrapper<SaleItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SaleItem::getOrderId, orderId);
        List<SaleItem> items = saleItemService.list(itemWrapper);

        for (SaleItem item : items) {
            LambdaQueryWrapper<Stock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(Stock::getMedId, item.getMedId())
                    .eq(Stock::getBatchNo, item.getBatchNo())
                    .eq(Stock::getStatus, 1)
                    .last("LIMIT 1 FOR UPDATE");
            Stock stock = stockService.getOne(stockWrapper, false);
            if (stock != null) {
                int stockNum = stock.getStockNum() == null ? 0 : stock.getStockNum();
                int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
                stock.setStockNum(stockNum + quantity);
                stockService.updateById(stock);
            }
        }

        subtractCustomerConsume(order);
        saleItemService.remove(itemWrapper);
    }

    private void addCustomerConsume(SaleOrder order) {
        if (order.getCustId() == null || order.getTotalPrice() == null) {
            return;
        }
        Customer customer = customerService.getById(order.getCustId());
        if (customer == null) {
            return;
        }
        BigDecimal current = customer.getTotalConsume() == null ? BigDecimal.ZERO : customer.getTotalConsume();
        customer.setTotalConsume(current.add(order.getTotalPrice()));
        customerService.updateById(customer);
    }

    private void subtractCustomerConsume(SaleOrder order) {
        if (order == null || order.getCustId() == null || order.getTotalPrice() == null) {
            return;
        }
        Customer customer = customerService.getById(order.getCustId());
        if (customer == null) {
            return;
        }
        BigDecimal current = customer.getTotalConsume() == null ? BigDecimal.ZERO : customer.getTotalConsume();
        customer.setTotalConsume(current.subtract(order.getTotalPrice()).max(BigDecimal.ZERO));
        customerService.updateById(customer);
    }

    private BigDecimal calculateTotalPrice(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private String getMedicineName(Integer medId) {
        Medicine medicine = medicineService.getById(medId);
        if (medicine == null || medicine.getMedName() == null || medicine.getMedName().trim().isEmpty()) {
            return "药品ID " + medId;
        }
        return medicine.getMedName();
    }
}
