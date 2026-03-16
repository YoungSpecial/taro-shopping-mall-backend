package com.mall.repository.impl;

import com.mall.model.Payment;
import com.mall.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockPaymentRepository implements PaymentRepository {

    private final ConcurrentHashMap<Long, Payment> payments = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> paymentNumberIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> orderIdToPaymentId = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idGenerator.getAndIncrement());
            payment.setCreatedAt(LocalDateTime.now());
        }

        if (payment.getPaymentNumber() != null) {
            paymentNumberIndex.put(payment.getPaymentNumber(), payment.getId());
        }

        if (payment.getOrderId() != null) {
            orderIdToPaymentId.put(payment.getOrderId(), payment.getId());
        }

        payments.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public Optional<Payment> findByPaymentNumber(String paymentNumber) {
        Long paymentId = paymentNumberIndex.get(paymentNumber);
        return paymentId != null ? Optional.ofNullable(payments.get(paymentId)) : Optional.empty();
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        Long paymentId = orderIdToPaymentId.get(orderId);
        return paymentId != null ? Optional.ofNullable(payments.get(paymentId)) : Optional.empty();
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return payments.values().stream()
                .filter(p -> userId.equals(p.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        return payments.values().stream()
                .filter(p -> status.equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Payment update(Payment payment) {
        return save(payment);
    }

    @Override
    public void deleteById(Long id) {
        Payment payment = payments.remove(id);
        if (payment != null) {
            if (payment.getPaymentNumber() != null) {
                paymentNumberIndex.remove(payment.getPaymentNumber());
            }
            if (payment.getOrderId() != null) {
                orderIdToPaymentId.remove(payment.getOrderId());
            }
        }
    }

    @Override
    public Boolean existsById(Long id) {
        return payments.containsKey(id);
    }
}
