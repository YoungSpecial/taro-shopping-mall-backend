package com.mall.repository.impl;

import com.mall.model.AdminUser;
import com.mall.repository.AdminUserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockAdminUserRepository implements AdminUserRepository {

    private final ConcurrentHashMap<Long, AdminUser> adminUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> usernameIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public AdminUser save(AdminUser adminUser) {
        if (adminUser.getId() == null) {
            adminUser.setId(idGenerator.getAndIncrement());
            adminUser.setCreatedAt(LocalDateTime.now());
        }
        adminUser.setUpdatedAt(LocalDateTime.now());

        adminUsers.put(adminUser.getId(), adminUser);
        if (adminUser.getUsername() != null) {
            usernameIndex.put(adminUser.getUsername(), adminUser.getId());
        }

        return adminUser;
    }

    @Override
    public Optional<AdminUser> findById(Long id) {
        return Optional.ofNullable(adminUsers.get(id));
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        Long userId = usernameIndex.get(username);
        return userId != null ? Optional.ofNullable(adminUsers.get(userId)) : Optional.empty();
    }

    @Override
    public List<AdminUser> findAll() {
        return new ArrayList<>(adminUsers.values());
    }

    @Override
    public List<AdminUser> findByRole(AdminUser.AdminRole role) {
        return adminUsers.values().stream()
                .filter(u -> role.equals(u.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminUser> findByStatus(AdminUser.AdminStatus status) {
        return adminUsers.values().stream()
                .filter(u -> status.equals(u.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public AdminUser update(AdminUser adminUser) {
        return save(adminUser);
    }

    @Override
    public void deleteById(Long id) {
        AdminUser adminUser = adminUsers.remove(id);
        if (adminUser != null && adminUser.getUsername() != null) {
            usernameIndex.remove(adminUser.getUsername());
        }
    }

    @Override
    public Boolean existsByUsername(String username) {
        return usernameIndex.containsKey(username);
    }

    @Override
    public Boolean existsById(Long id) {
        return adminUsers.containsKey(id);
    }
}
