package com.mall.service;

import com.mall.model.User;
import com.mall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeChatAuthService weChatAuthService;

    public User loginOrRegister(String wechatCode) {
        String openId = weChatAuthService.mockExchangeCodeForOpenId(wechatCode);
        if (openId == null) {
            throw new RuntimeException("Invalid WeChat code");
        }

        User user = userRepository.findByWechatOpenId(openId).orElse(null);

        if (user == null) {
            user = new User();
            user.setId(System.currentTimeMillis() % 1000000);
            user.setWechatOpenId(openId);
            user.setWechatUnionId("mock_unionid_" + openId);
            user.setNickname("新用户" + System.currentTimeMillis() % 10000);
            user.setAvatarUrl("/avatars/default.png");
            user.setStatus(User.UserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        } else {
            user.setLastLoginAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        return user;
    }
}
