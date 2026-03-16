package com.mall.config;

import com.mall.model.*;
import com.mall.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class MockDataInitializer {

    @Bean
    public CommandLineRunner initMockData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            AdminUserRepository adminUserRepository
    ) {
        return args -> {
            System.out.println("Initializing mock data for testing...");
            
            // 创建测试管理员用户
            AdminUser adminUser = new AdminUser();
            adminUser.setUsername("admin");
            adminUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwE6e"); // password123
            adminUser.setEmail("admin@example.com");
            adminUser.setRole(AdminUser.AdminRole.ADMIN);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            adminUserRepository.save(adminUser);
            
            System.out.println("Admin user created: admin / password123");
            System.out.println("Mock data initialization completed.");
        };
    }
}