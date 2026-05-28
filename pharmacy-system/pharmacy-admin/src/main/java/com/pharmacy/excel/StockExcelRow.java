package com.pharmacy.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockExcelRow {
    @ExcelProperty("库存ID")
    private Integer stockId;

    @ExcelProperty("药品ID")
    private Integer medId;

    @ExcelProperty("药品名称")
    private String medName;

    @ExcelProperty("规格")
    private String spec;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("供应商")
    private String supplierName;

    @ExcelProperty("批号")
    private String batchNo;

    @ExcelProperty("库存数量")
    private Integer stockNum;

    @ExcelProperty("库存预警下限")
    private Integer stockMin;

    @ExcelProperty("采购价")
    private BigDecimal purchasePrice;

    @ExcelProperty("生产日期")
    private String productionDate;

    @ExcelProperty("有效期至")
    private String expireDate;

    @ExcelProperty("存放位置")
    private String cabinet;

    @ExcelProperty("状态")
    private String statusText;

    @ExcelProperty("备注")
    private String remark;
}
