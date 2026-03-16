package com.mall.dto;

public record TokenRefreshResponse(
        String token,
        String tokenType,
        String expiresIn
) {
}
