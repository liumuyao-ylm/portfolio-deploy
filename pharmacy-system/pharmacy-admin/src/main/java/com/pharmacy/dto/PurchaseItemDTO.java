package com.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseItemDTO {
    private Integer medId;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expireDate;
    private Integer purchaseNum;
    private BigDecimal purchasePrice;
    private BigDecimal totalPrice;
    private String cabinet;
    private String remark;
}
