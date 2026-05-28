package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("purchase_order")
public class PurchaseOrder {
    @TableId(value = "purchase_id", type = IdType.AUTO)
    private Long purchaseId;

    @TableField("supplier_id")
    private Integer supplierId;

    @TableField(exist = false)
    private String supplierName;

    @TableField("user_id")
    private Integer userId;

    @TableField(exist = false)
    private String userRealName;

    @TableField("purchase_time")
    private LocalDateTime purchaseTime;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("pay_type")
    private String payType;

    @TableField("purchase_status")
    private Integer purchaseStatus;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
