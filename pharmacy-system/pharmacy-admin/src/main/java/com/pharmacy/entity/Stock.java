package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("stock")
public class Stock {
    @TableId(value = "stock_id", type = IdType.AUTO)
    private Integer stockId;

    @TableField("med_id")
    private Integer medId;

    @TableField("batch_no")
    private String batchNo;

    @TableField("expire_date")
    private LocalDate expireDate;

    @TableField("stock_num")
    private Integer stockNum;

    @TableField("stock_min")
    private Integer stockMin;

    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    @TableField("cabinet")
    private String cabinet;

    @TableField("production_date")
    private LocalDate productionDate;

    @TableField("supplier_id")
    private Integer supplierId;

    @TableField("purchase_id")
    private Long purchaseId;

    @TableField("purchase_item_id")
    private Long purchaseItemId;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
