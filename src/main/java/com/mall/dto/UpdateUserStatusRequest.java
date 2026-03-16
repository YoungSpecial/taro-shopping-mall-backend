package com.mall.dto;

import com.mall.model.User;

public record UpdateUserStatusRequest(
        User.UserStatus status
) {
}