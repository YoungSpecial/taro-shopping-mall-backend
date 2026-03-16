package com.mall.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {

    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long addressId;
    private String addressDetail;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private BigDecimal shippingFee;
    private OrderStatus status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items;

    public enum OrderStatus {
        PENDING_PAYMENT,
        PAID,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    public Order(Long userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING_PAYMENT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 生成订单号的辅助方法
    public void generateOrderNumber() {
        this.orderNumber = "ORD" + String.format("%08d", this.id) + System.currentTimeMillis() % 10000;
    }

    // 计算总金额的辅助方法
    public void calculateAmounts() {
        if (this.items != null && !this.items.isEmpty()) {
            this.totalAmount = this.items.stream()
                    .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (this.discountAmount == null) {
                this.discountAmount = BigDecimal.ZERO;
            }
            
            if (this.shippingFee == null) {
                this.shippingFee = BigDecimal.ZERO;
            }
            
            this.actualAmount = this.totalAmount
                    .subtract(this.discountAmount)
                    .add(this.shippingFee);
        } else {
            this.totalAmount = BigDecimal.ZERO;
            this.discountAmount = BigDecimal.ZERO;
            this.shippingFee = BigDecimal.ZERO;
            this.actualAmount = BigDecimal.ZERO;
        }
    }
}
