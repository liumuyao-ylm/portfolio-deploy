package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("medicine")
public class Medicine {
    @TableId(value = "med_id", type = IdType.AUTO)
    private Integer medId;

    @TableField("med_name")
    private String medName;

    @TableField("med_alias")
    private String medAlias;

    @TableField(exist = false)
    private String supplierName;

    @TableField("med_type")
    private String medType;

    @TableField("spec")
    private String spec;

    @TableField("unit")
    private String unit;

    @TableField("dosage_form")
    private String dosageForm;

    @TableField("manufacturer")
    private String manufacturer;

    @TableField("approval_no")
    private String approvalNo;

    @TableField("retail_price")
    private BigDecimal retailPrice;

    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    @TableField("is_rx")
    private Integer isRx;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
