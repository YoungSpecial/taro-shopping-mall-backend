package com.mall.dto;

public record CategoryRequest(
        String name,
        String description,
        String iconUrl,
        Integer sortOrder,
        Long parentId,
        Integer level
) {
}