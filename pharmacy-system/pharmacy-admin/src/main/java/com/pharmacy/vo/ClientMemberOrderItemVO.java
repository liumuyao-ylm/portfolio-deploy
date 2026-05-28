package com.pharmacy.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientMemberOrderItemVO {
    private Long itemId;
    private Integer medicineId;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
