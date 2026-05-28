package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {
    @TableId(value = "cust_id", type = IdType.AUTO)
    private Integer custId;

    @TableField("cust_name")
    private String custName;

    @TableField("phone")
    private String phone;

    @TableField("is_member")
    private Integer isMember;

    @TableField("member_level")
    private String memberLevel;

    @TableField("total_consume")
    private BigDecimal totalConsume;

    @TableField("birthday")
    private LocalDate birthday;

    @TableField("address")
    private String address;

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
