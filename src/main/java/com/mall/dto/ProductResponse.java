package com.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    Long id;
    String name;
    String description;
    String imageUrl;
    BigDecimal price;
    BigDecimal originalPrice;
    Integer stock;
    Integer sold;
    com.mall.model.ProductStatus status;
    Long categoryId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Double rating;
    List<String> images;

    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString();
    }
}