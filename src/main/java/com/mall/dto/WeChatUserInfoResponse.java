package com.mall.dto;

public record WeChatUserInfoResponse(
        Integer errcode,
        String errmsg,
        String openid,
        String nickname,
        Integer sex,
        String province,
        String city,
        String country,
        String headimgurl,
        String privilege
) {
    public boolean isSuccess() {
        return errcode == null || errcode == 0;
    }
}
