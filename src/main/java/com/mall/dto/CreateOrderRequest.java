package com.mall.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private CreateOrderAddressRequest address;
    private List<CreateOrderItemsRequest> items;
    private String paymentMethod;
}