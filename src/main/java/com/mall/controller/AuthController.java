package com.mall.controller;

import com.mall.dto.AuthResponse;
import com.mall.dto.WeChatLoginRequest;
import com.mall.model.User;
import com.mall.security.JwtService;
import com.mall.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/wechat/login")
    public ResponseEntity<AuthResponse> weChatLogin(@RequestBody WeChatLoginRequest request) {
        User user = authService.loginOrRegister(request.code());

        Map<String, String> tokens = jwtService.generateTokensForUser(user);

        AuthResponse.UserDto userDto = new AuthResponse.UserDto(
                user.getId().toString(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getPhone(),
                user.getEmail()
        );

        AuthResponse response = new AuthResponse(
                tokens.get("token"),
                tokens.get("tokenType"),
                tokens.get("expiresIn"),
                userDto
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<com.mall.dto.TokenRefreshResponse> refreshToken(
            @AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {
        String username = userDetails.getUsername();

        Map<String, String> tokens = jwtService.generateTokensForUser(
                userRepository.findByWechatOpenId(username).orElseThrow()
        );

        com.mall.dto.TokenRefreshResponse response = new com.mall.dto.TokenRefreshResponse(
                tokens.get("token"),
                tokens.get("tokenType"),
                tokens.get("expiresIn")
        );

        return ResponseEntity.ok(response);
    }

    @Autowired
    private com.mall.repository.UserRepository userRepository;
    
    @Autowired
    private com.mall.repository.AdminUserRepository adminUserRepository;

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody com.mall.dto.AdminLoginRequest request) {
        // 简单验证用户名和密码
        if (!"admin".equals(request.username()) || !"password123".equals(request.password())) {
            return ResponseEntity.status(401).body(new AuthResponse(null, null, null, null));
        }
        
        // 直接生成令牌，避免复杂的认证流程
        com.mall.model.User userForToken = new com.mall.model.User();
        userForToken.setId(1L); // 管理员ID
        userForToken.setWechatOpenId("admin_openid");
        userForToken.setNickname("管理员");
        userForToken.setAvatarUrl("/avatars/admin.png");

        Map<String, String> tokens = jwtService.generateTokensForUser(userForToken);

        AuthResponse.UserDto userDto = new AuthResponse.UserDto(
                userForToken.getId().toString(),
                userForToken.getNickname(),
                userForToken.getAvatarUrl(),
                null,
                null
        );

        AuthResponse response = new AuthResponse(
                tokens.get("token"),
                tokens.get("tokenType"),
                tokens.get("expiresIn"),
                userDto
        );

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/user/login")
    public ResponseEntity<AuthResponse> userLogin(@RequestBody com.mall.dto.AdminLoginRequest request) {
        // 简单验证用户名和密码
        if (!"user".equals(request.username()) || !"password123".equals(request.password())) {
            return ResponseEntity.status(401).body(new AuthResponse(null, null, null, null));
        }
        
        // 直接生成令牌，避免复杂的认证流程
        com.mall.model.User userForToken = new com.mall.model.User();
        userForToken.setId(100L); // 普通用户ID
        userForToken.setWechatOpenId("user_openid");
        userForToken.setNickname("测试用户");
        userForToken.setAvatarUrl("/avatars/user.png");
        userForToken.setPhone("13800138000");
        userForToken.setEmail("user@example.com");

        Map<String, String> tokens = jwtService.generateTokensForUser(userForToken);

        AuthResponse.UserDto userDto = new AuthResponse.UserDto(
                userForToken.getId().toString(),
                userForToken.getNickname(),
                userForToken.getAvatarUrl(),
                userForToken.getPhone(),
                userForToken.getEmail()
        );

        AuthResponse response = new AuthResponse(
                tokens.get("token"),
                tokens.get("tokenType"),
                tokens.get("expiresIn"),
                userDto
        );

        return ResponseEntity.ok(response);
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok().build();
    }
}
