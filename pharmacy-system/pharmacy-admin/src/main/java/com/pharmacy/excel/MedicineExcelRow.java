package com.pharmacy.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicineExcelRow {
    @ExcelProperty("药品ID")
    private Integer medId;

    @ExcelProperty("药品名称")
    private String medName;

    @ExcelProperty("别名")
    private String medAlias;

    @ExcelProperty("药品类型")
    private String medType;

    @ExcelProperty("规格")
    private String spec;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("剂型")
    private String dosageForm;

    @ExcelProperty("生产厂家")
    private String manufacturer;

    @ExcelProperty("批准文号")
    private String approvalNo;

    @ExcelProperty("零售价")
    private BigDecimal retailPrice;

    @ExcelProperty("采购价")
    private BigDecimal purchasePrice;

    @ExcelProperty("是否处方药")
    private String isRxText;

    @ExcelProperty("状态")
    private String statusText;

    @ExcelProperty("备注")
    private String remark;
}
