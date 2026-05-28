package com.pharmacy.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderExcelRow {
    @ExcelProperty("采购单ID")
    private Long purchaseId;

    @ExcelProperty("供应商")
    private String supplierName;

    @ExcelProperty("操作员")
    private String userRealName;

    @ExcelProperty("采购时间")
    private String purchaseTime;

    @ExcelProperty("总金额")
    private BigDecimal totalAmount;

    @ExcelProperty("支付方式")
    private String payType;

    @ExcelProperty("采购状态")
    private String purchaseStatusText;

    @ExcelProperty("备注")
    private String remark;
}
