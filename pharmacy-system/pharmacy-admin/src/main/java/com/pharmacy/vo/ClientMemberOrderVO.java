package com.pharmacy.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientMemberOrderVO {
    private Long orderId;
    private LocalDateTime createTime;
    private BigDecimal totalPrice;
    private String payType;
    private String orderTypeText;
    private String payStatusText;
    private String summary;
    private List<ClientMemberOrderItemVO> items;
}
