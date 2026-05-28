package com.pharmacy.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientStoreInfoVO {
    private String name;
    private String status;
    private String hours;
    private String phone;
    private String address;
    private String scope;
    private String notice;
    private String locationText;
    private String routeText;
    private List<String> services;
}
