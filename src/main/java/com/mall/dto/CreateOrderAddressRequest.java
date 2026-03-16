package com.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateOrderAddressRequest {
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String address;
    private Boolean isDefault;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}