package com.pharmacy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pharmacy.dto.PurchaseOrderCreateDTO;
import com.pharmacy.entity.PurchaseOrder;

public interface PurchaseOrderService extends IService<PurchaseOrder> {
    PurchaseOrder createPurchaseOrder(PurchaseOrderCreateDTO dto);

    PurchaseOrder confirmInbound(Long purchaseId);

    void deletePurchaseOrder(Long purchaseId);
}
