package com.mall.security;

import com.mall.model.AdminUser;
import com.mall.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));

        String role = adminUser.getRole() == AdminUser.AdminRole.SUPER_ADMIN ? "ROLE_ADMIN,ROLE_SUPER_ADMIN" : "ROLE_ADMIN";

        return org.springframework.security.core.userdetails.User
                .withUsername(adminUser.getUsername())
                .password(adminUser.getPassword())
                .roles(adminUser.getRole().name())
                .accountExpired(adminUser.getStatus() != AdminUser.AdminStatus.ACTIVE)
                .accountLocked(adminUser.getStatus() != AdminUser.AdminStatus.ACTIVE)
                .credentialsExpired(false)
                .disabled(adminUser.getStatus() != AdminUser.AdminStatus.ACTIVE)
                .build();
    }
}
