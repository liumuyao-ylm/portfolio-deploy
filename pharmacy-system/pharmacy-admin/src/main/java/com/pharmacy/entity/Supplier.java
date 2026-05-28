package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("supplier")
public class Supplier {
    @TableId(value = "supplier_id", type = IdType.AUTO)
    private Integer supplierId;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("short_name")
    private String shortName;

    @TableField("contact")
    private String contact;

    @TableField("phone")
    private String phone;

    @TableField("telephone")
    private String telephone;

    @TableField("address")
    private String address;

    @TableField("business_license")
    private String businessLicense;

    @TableField("supply_type")
    private String supplyType;

    @TableField("bank_info")
    private String bankInfo;

    @TableField("status")
    @TableLogic
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
