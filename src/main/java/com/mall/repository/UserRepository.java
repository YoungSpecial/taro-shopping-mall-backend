package com.mall.repository;

import com.mall.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByWechatOpenId(String openId);

    Optional<User> findByPhone(String phone);

    List<User> findAll();

    void deleteById(Long id);

    User update(User user);

    Boolean existsByWechatOpenId(String openId);

    Boolean existsByPhone(String phone);
}
