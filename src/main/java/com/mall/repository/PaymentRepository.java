package com.mall.repository;

import com.mall.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    Payment update(Payment payment);

    void deleteById(Long id);

    Boolean existsById(Long id);
}
