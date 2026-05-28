package com.pharmacy.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientMedicineVO {
    private Integer id;
    private String name;
    private String medAlias;
    private String medType;
    private String spec;
    private String unit;
    private String dosageForm;
    private String manufacturer;
    private String approvalNo;
    private BigDecimal retailPrice;
    private Integer isRx;
    private Integer stockCount;
    private String stockStatus;
}
