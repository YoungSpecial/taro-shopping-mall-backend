package com.mall.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.dto.WeChatAuthResponse;
import com.mall.dto.WeChatUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeChatAuthService {

    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

    private static final String JS_CODE_TO_SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeChatAuthResponse getOpenIdByCode(String code) {
        String url = String.format(JS_CODE_TO_SESSION_URL, appId, appSecret, code);
        return restTemplate.getForObject(url, WeChatAuthResponse.class);
    }

    public String mockExchangeCodeForOpenId(String code) {
        if ("mock_code_001".equals(code)) {
            return "mock_openid_001";
        }
        return null;
    }
}
