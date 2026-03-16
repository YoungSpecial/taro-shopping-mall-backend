package com.mall.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Integer sortOrder;
    private Long parentId;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
