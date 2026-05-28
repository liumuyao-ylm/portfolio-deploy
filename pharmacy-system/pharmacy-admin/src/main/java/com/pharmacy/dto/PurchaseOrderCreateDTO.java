package com.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderCreateDTO {
    private Integer supplierId;
    private Integer userId;
    private LocalDateTime purchaseTime;
    private BigDecimal totalAmount;
    private String payType;
    private String remark;
    private List<PurchaseItemDTO> items;
}
