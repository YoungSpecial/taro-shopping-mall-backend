package com.mall.dto;

public record AddToCartRequest(
        Long productId,
        Integer quantity
) {
    public AddToCartRequest {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
    }
}