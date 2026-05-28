package com.pharmacy.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClientMemberProfileVO {
    private Integer customerId;
    private String name;
    private String phone;
    private String memberLevel;
    private BigDecimal totalConsume;
    private LocalDate birthday;
    private String address;
    private String remark;
    private String statusText;
}
