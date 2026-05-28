package com.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleOrderCreateDTO {
    private Long orderId;
    private Integer custId;
    private Integer userId;
    private BigDecimal totalPrice;
    private String payType;
    private Integer orderType;
    private Integer payStatus;
    private String remark;
    private List<SaleItemDTO> items;
}
