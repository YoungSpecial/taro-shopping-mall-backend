package com.mall.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address {
    private Long id;
    private Long userId;
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
