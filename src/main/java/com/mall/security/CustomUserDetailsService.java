package com.mall.security;

import com.mall.model.User;
import com.mall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByWechatOpenId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with openid: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getWechatOpenId())
                .password("") // WeChat users don't have passwords
                .authorities("ROLE_USER")
                .accountExpired(user.getStatus() != User.UserStatus.ACTIVE)
                .accountLocked(user.getStatus() != User.UserStatus.ACTIVE)
                .credentialsExpired(false)
                .disabled(user.getStatus() != User.UserStatus.ACTIVE)
                .build();
    }
}
