package com.pharmacy.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StockVO {
    private Integer stockId;
    private Integer medId;
    private String medName;
    private String supplierName;
    private String spec;
    private String unit;
    private String batchNo;
    private LocalDate expireDate;
    private Integer stockNum;
    private BigDecimal purchasePrice;
    private String cabinet;
    private LocalDate productionDate;
    private Integer supplierId;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer stockMin;
    private Boolean isExpired;
    private Boolean isNearExpire;
    private Boolean isLowStock;
}
