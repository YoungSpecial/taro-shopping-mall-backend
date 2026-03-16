package com.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateOrderItemsRequest {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private String name;
    private String image;
}