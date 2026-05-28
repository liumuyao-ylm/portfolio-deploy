package com.pharmacy.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultVO {
    private int successCount;
    private int failCount;
    private List<String> errors = new ArrayList<>();

    public void addSuccess() {
        successCount++;
    }

    public void addError(String error) {
        failCount++;
        errors.add(error);
    }
}
