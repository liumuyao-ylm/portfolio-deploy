package com.pharmacy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pharmacy.mapper")
public class PharmacyAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmacyAdminApplication.class, args);
    }
}
