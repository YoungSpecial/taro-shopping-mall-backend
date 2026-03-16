package com.mall.dto;

import lombok.Data;

@Data
public class OrderDetailResponse extends OrderResponse{
    private AddressResponse  shippingAddress;
}