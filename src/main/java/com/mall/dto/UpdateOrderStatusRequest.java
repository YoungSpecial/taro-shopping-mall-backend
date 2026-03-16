package com.mall.dto;

import com.mall.model.Order;

public class UpdateOrderStatusRequest {
    private Order.OrderStatus status;
    private String trackingNumber;
    private String remark;

    // Getters
    public Order.OrderStatus getStatus() { return status; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getRemark() { return remark; }

    // Setters
    public void setStatus(Order.OrderStatus status) { this.status = status; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setRemark(String remark) { this.remark = remark; }
}