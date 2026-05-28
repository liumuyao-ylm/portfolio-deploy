package com.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemDTO {
    private Integer medId;
    private String batchNo;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
