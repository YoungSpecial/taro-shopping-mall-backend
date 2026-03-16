package com.mall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        Integer totalItems,
        BigDecimal subtotal,
        BigDecimal shipping,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal total,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}