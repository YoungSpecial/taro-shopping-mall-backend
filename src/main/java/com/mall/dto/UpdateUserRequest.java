package com.mall.dto;

public record UpdateUserRequest(
        String nickname,
        String avatarUrl,
        String phone,
        String email
) {
}
