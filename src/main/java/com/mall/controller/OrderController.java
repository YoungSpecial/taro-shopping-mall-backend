package com.mall.controller;

import com.mall.dto.*;
import com.mall.model.Order;
import com.mall.model.Product;
import com.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "订单管理", description = "订单创建、查询和管理接口")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //购物车应该是单独的表保存，这里进行简化，从前端获取
    @PostMapping
    @Operation(summary = "从购物车创建订单", description = "从当前用户的购物车创建新订单")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {
        // 如果userId为null（测试时），使用默认用户ID
        OrderResponse order = orderService.createOrderFromCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable Long orderId) {
        OrderDetailResponse order = orderService.getOrderById(orderId, 1L);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/list")
    @Operation(summary = "获取用户订单列表", description = "获取当前用户的所有订单")
    public ResponseEntity<PaginatedResponse<OrderResponse>> getUserOrders(@RequestBody QueryOrderRequest request) {
        List<OrderResponse> orders = orderService.getUserOrders(request,1L);

        int page = request.getPage() > 0 ? request.getPage() : 1;
        int size = request.getSize() > 0 ? request.getSize() : 10;
        int totalElements = orders.size();
        int start = Math.min((page - 1) * size, totalElements);
        int end = Math.min(start + size, totalElements);
        List<OrderResponse> paginatedProducts = orders.subList(start, end);

        PaginatedResponse<OrderResponse> response = PaginatedResponse.of(
                paginatedProducts, page, size, totalElements
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态（需要管理员权限）")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse order = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消待支付状态的订单")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        OrderResponse order = orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(order);
    }
}