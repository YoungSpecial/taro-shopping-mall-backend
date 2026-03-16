package com.mall.dto;

public record UpdateCartItemRequest(
        Integer quantity
) {
    public UpdateCartItemRequest {
        if (quantity == null || quantity < 0) {
            quantity = 0;
        }
    }
}