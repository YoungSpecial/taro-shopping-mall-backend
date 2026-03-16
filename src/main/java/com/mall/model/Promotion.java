package com.mall.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Promotion {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private PromotionType type;
    private BigDecimal discountValue;
    private BigDecimal minimumCartAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum PromotionType {
        PERCENTAGE_DISCOUNT,
        FIXED_AMOUNT_DISCOUNT,
        FREE_SHIPPING
    }
}