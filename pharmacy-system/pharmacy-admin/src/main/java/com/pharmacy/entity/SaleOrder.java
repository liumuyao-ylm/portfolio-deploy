package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sale_order")
public class SaleOrder {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @TableField("cust_id")
    private Integer custId;

    @TableField(exist = false)
    private String custName;

    @TableField("user_id")
    private Integer userId;

    @TableField(exist = false)
    private String userRealName;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("pay_type")
    private String payType;

    @TableField("order_type")
    private Integer orderType;

    @TableField("pay_status")
    private Integer payStatus;

    @TableField("remark")
    private String remark;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
