package com.mall.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponse {
    Long id;
    String name;
    Integer count;

    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString();
    }
}