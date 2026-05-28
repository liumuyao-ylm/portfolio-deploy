package com.pharmacy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pharmacy.dto.SaleOrderCreateDTO;
import com.pharmacy.entity.SaleOrder;

public interface SaleOrderService extends IService<SaleOrder> {
    SaleOrder createSaleOrder(SaleOrderCreateDTO dto);

    SaleOrder confirmOutbound(Long orderId);

    void deleteSaleOrder(Long orderId);
}
