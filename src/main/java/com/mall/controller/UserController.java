package com.mall.controller;

import com.mall.dto.UpdateUserRequest;
import com.mall.dto.UserDto;
import com.mall.model.User;
import com.mall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByWechatOpenId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto userDto = new UserDto(
                user.getId().toString(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus().name(),
                formatDate(user.getCreatedAt()),
                formatDate(user.getLastLoginAt())
        );

        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserRequest request) {

        User user = userRepository.findByWechatOpenId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.nickname() != null && !request.nickname().isEmpty()) {
            user.setNickname(request.nickname());
        }
        if (request.avatarUrl() != null && !request.avatarUrl().isEmpty()) {
            user.setAvatarUrl(request.avatarUrl());
        }
        if (request.phone() != null && !request.phone().isEmpty()) {
            user.setPhone(request.phone());
        }
        if (request.email() != null && !request.email().isEmpty()) {
            user.setEmail(request.email());
        }
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        UserDto userDto = new UserDto(
                user.getId().toString(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus().name(),
                formatDate(user.getCreatedAt()),
                formatDate(user.getLastLoginAt())
        );

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/me/sync-wechat")
    public ResponseEntity<UserDto> syncWithWeChat(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByWechatOpenId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setNickname("微信用户_" + System.currentTimeMillis() % 10000);
        user.setAvatarUrl("/avatars/wechat-default.png");
        user.setUpdatedAt(LocalDateTime.now());
        
        user = userRepository.save(user);
        
        UserDto userDto = new UserDto(
                user.getId().toString(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus().name(),
                formatDate(user.getCreatedAt()),
                formatDate(user.getLastLoginAt())
        );
        
        return ResponseEntity.ok(userDto);
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
