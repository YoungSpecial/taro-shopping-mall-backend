package com.mall.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer stock,
        com.mall.model.ProductStatus status,
        Long categoryId
) {
}