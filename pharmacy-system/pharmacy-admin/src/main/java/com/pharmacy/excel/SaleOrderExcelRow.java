package com.pharmacy.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleOrderExcelRow {
    @ExcelProperty("销售单ID")
    private Long orderId;

    @ExcelProperty("客户")
    private String custName;

    @ExcelProperty("操作员")
    private String userRealName;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("总金额")
    private BigDecimal totalPrice;

    @ExcelProperty("支付方式")
    private String payType;

    @ExcelProperty("订单类型")
    private String orderTypeText;

    @ExcelProperty("支付状态")
    private String payStatusText;

    @ExcelProperty("备注")
    private String remark;
}
