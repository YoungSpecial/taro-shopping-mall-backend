package com.mall.dto;

import lombok.Data;

@Data
public class BasePageRequest {
    int page;
    int size;
    String sort;
}
