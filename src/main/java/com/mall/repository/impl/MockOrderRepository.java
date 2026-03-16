package com.mall.repository.impl;

import com.mall.dto.QueryOrderRequest;
import com.mall.model.Order;
import com.mall.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockOrderRepository implements OrderRepository {

    private final ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> orderNumberIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
        } else {
            order.setUpdatedAt(LocalDateTime.now());
        }

        if (order.getOrderNumber() != null) {
            orderNumberIndex.put(order.getOrderNumber(), order.getId());
        }

        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        Long orderId = orderNumberIndex.get(orderNumber);
        return orderId != null ? Optional.ofNullable(orders.get(orderId)) : Optional.empty();
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orders.values().stream()
                .filter(o -> userId.equals(o.getUserId()))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orders.values().stream()
                .filter(o -> status.equals(o.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Order update(Order order) {
        return save(order);
    }

    @Override
    public void deleteById(Long id) {
        Order order = orders.remove(id);
        if (order != null && order.getOrderNumber() != null) {
            orderNumberIndex.remove(order.getOrderNumber());
        }
    }

    @Override
    public List<Order> findRecentOrders(int limit) {
        return orders.values().stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean existsById(Long id) {
        return orders.containsKey(id);
    }

    @Override
    public Long countByUserId(Long userId) {
        return orders.values().stream()
                .filter(o -> userId.equals(o.getUserId()))
                .count();
    }

    @Override
    public List<Order> findOrders(Long userId, String status) {
        return orders.values().stream()
                .filter(o -> userId.equals(o.getUserId()))
                .filter(o -> {
                    if(null == status){
                        return true;
                    }
                    return status.equals(o.getStatus().name());
                })
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
