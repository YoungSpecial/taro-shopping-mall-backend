package com.mall.dto;

public record UserDto(
        String id,
        String nickname,
        String avatarUrl,
        String phone,
        String email,
        String status,
        String createdAt,
        String lastLoginAt
) {
}
