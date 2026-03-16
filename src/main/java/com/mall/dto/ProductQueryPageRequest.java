package com.mall.dto;

import lombok.Data;

@Data
public class ProductQueryPageRequest extends BasePageRequest {
    String search;
    Long categoryId;
    String status;
    Double minRating;
    private String sortBy;
}
