package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_check")
public class StockCheck {
    @TableId(value = "check_id", type = IdType.AUTO)
    private Long checkId;

    @TableField("check_no")
    private String checkNo;

    @TableField("med_id")
    private Integer medId;

    @TableField("batch_no")
    private String batchNo;

    @TableField("system_stock")
    private Integer systemStock;

    @TableField("actual_stock")
    private Integer actualStock;

    @TableField("profit_loss")
    private Integer profitLoss;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("profit_loss_amount")
    private BigDecimal profitLossAmount;

    @TableField("check_user")
    private Integer checkUser;

    @TableField("check_time")
    private LocalDateTime checkTime;

    @TableField("reason")
    private String reason;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
