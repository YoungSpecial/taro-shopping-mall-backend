package com.mall.repository.impl;

import com.mall.model.User;
import com.mall.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockUserRepository implements UserRepository {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> wechatOpenIdIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> phoneIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
        } else {
            user.setUpdatedAt(LocalDateTime.now());
        }

        users.put(user.getId(), user);

        if (user.getWechatOpenId() != null) {
            wechatOpenIdIndex.put(user.getWechatOpenId(), user.getId());
        }

        if (user.getPhone() != null) {
            phoneIndex.put(user.getPhone(), user.getId());
        }

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByWechatOpenId(String openId) {
        Long userId = wechatOpenIdIndex.get(openId);
        return userId != null ? Optional.ofNullable(users.get(userId)) : Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        Long userId = phoneIndex.get(phone);
        return userId != null ? Optional.ofNullable(users.get(userId)) : Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        User user = users.remove(id);
        if (user != null) {
            if (user.getWechatOpenId() != null) {
                wechatOpenIdIndex.remove(user.getWechatOpenId());
            }
            if (user.getPhone() != null) {
                phoneIndex.remove(user.getPhone());
            }
        }
    }

    @Override
    public User update(User user) {
        return save(user);
    }

    @Override
    public Boolean existsByWechatOpenId(String openId) {
        return wechatOpenIdIndex.containsKey(openId);
    }

    @Override
    public Boolean existsByPhone(String phone) {
        return phoneIndex.containsKey(phone);
    }
}
