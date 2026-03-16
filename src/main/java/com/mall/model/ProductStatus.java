package com.mall.model;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    OUT_OF_STOCK
}