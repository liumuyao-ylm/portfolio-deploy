package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_item")
public class PurchaseItem {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    @TableField("purchase_id")
    private Long purchaseId;

    @TableField("med_id")
    private Integer medId;

    @TableField(exist = false)
    private String medName;

    @TableField("batch_no")
    private String batchNo;

    @TableField("production_date")
    private LocalDate productionDate;

    @TableField("expire_date")
    private LocalDate expireDate;

    @TableField("purchase_num")
    private Integer purchaseNum;

    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("cabinet")
    private String cabinet;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
