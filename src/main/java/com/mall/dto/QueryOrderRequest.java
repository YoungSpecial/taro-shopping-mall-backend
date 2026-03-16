package com.mall.dto;

import lombok.Data;

@Data
public class QueryOrderRequest extends BasePageRequest {
    private String orderNumber;
    private Long userId;
    private String status;
}
