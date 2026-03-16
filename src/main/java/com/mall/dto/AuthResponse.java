package com.mall.dto;

import java.util.Map;

public record AuthResponse(
        String token,
        String tokenType,
        String expiresIn,
        UserDto user
) {
    public record UserDto(
            String id,
            String nickname,
            String avatarUrl,
            String phone,
            String email
    ) {
    }
}
