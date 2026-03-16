package com.mall.repository;

import com.mall.model.AdminUser;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository {

    AdminUser save(AdminUser adminUser);

    Optional<AdminUser> findById(Long id);

    Optional<AdminUser> findByUsername(String username);

    List<AdminUser> findAll();

    List<AdminUser> findByRole(AdminUser.AdminRole role);

    List<AdminUser> findByStatus(AdminUser.AdminStatus status);

    AdminUser update(AdminUser adminUser);

    void deleteById(Long id);

    Boolean existsByUsername(String username);

    Boolean existsById(Long id);
}
