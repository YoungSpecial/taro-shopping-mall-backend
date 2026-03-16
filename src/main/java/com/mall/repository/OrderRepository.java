package com.mall.repository;

import com.mall.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findAll();

    Order update(Order order);

    void deleteById(Long id);

    List<Order> findRecentOrders(int limit);

    Boolean existsById(Long id);

    Long countByUserId(Long userId);

    List<Order> findOrders(Long userId, String status);
}
