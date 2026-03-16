package com.mall.dto;

public record AddressRequest(
        String receiverName,
        String receiverPhone,
        String province,
        String city,
        String district,
        String detailAddress,
        Boolean isDefault
) {
}