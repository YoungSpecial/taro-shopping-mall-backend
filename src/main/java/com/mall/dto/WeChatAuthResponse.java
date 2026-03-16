package com.mall.dto;

import java.util.Map;

public record WeChatAuthResponse(
        Integer errcode,
        String errmsg,
        String openid,
        String unionid,
        String session_key,
        Map<String, Object> other
) {
    public boolean isSuccess() {
        return errcode == null || errcode == 0;
    }
}
