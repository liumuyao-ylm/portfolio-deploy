package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sale_item")
public class SaleItem {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    @TableField("order_id")
    private Long orderId;

    @TableField("med_id")
    private Integer medId;

    @TableField(exist = false)
    private String medName;

    @TableField("batch_no")
    private String batchNo;

    @TableField("quantity")
    private Integer quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
