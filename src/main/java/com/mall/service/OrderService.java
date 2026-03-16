package com.mall.service;

import com.mall.dto.*;
import com.mall.exception.BusinessException;
import com.mall.exception.ResourceNotFoundException;
import com.mall.model.*;
import com.mall.repository.*;
import com.mall.repository.impl.MockAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository,
                        ShoppingCartRepository shoppingCartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository, AddressRepository mockAddressRepository) {
        this.orderRepository = orderRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.addressRepository = mockAddressRepository;
    }

    public OrderResponse createOrderFromCart(CreateOrderRequest request) {
        // 3. 验证库存和价格
        validateCartItems(request.getItems());

        Address address = new Address();
        BeanUtils.copyProperties(request.getAddress(), address);
        address.setUserId(1L);
        Address savedAddress = addressRepository.save(address);
        // 4. 创建订单
        Order order = new Order(1L);
        //这里设置为付款完成
        order.setStatus(Order.OrderStatus.PAID);
        order.setUserName("test");
        order.setUserPhone("13800138000");
        order.setAddressId(savedAddress.getId());
        order.setShippingFee(BigDecimal.ZERO);

        // 5. 转换购物车项为订单项
        List<OrderItem> orderItems = request.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setProductName(cartItem.getName());
                    orderItem.setProductImage(cartItem.getImage());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setTotalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.calculateAmounts();

        // 6. 保存订单
        order = orderRepository.save(order);
        order.generateOrderNumber();
        order = orderRepository.update(order);

        // 7. 减少库存
        reduceInventory(orderItems);

        // 8. 清空购物车
//        shoppingCartRepository.clearCart(userId);

        log.info("Order created successfully: {}", order.getOrderNumber());
        return OrderResponse.fromOrder(order);
    }

    private void validateCartItems(List<CreateOrderItemsRequest> carts) {
        for (CreateOrderItemsRequest cartItem : carts) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("商品未发现: " + cartItem.getProductId()));

            // 检查库存
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("没有足够的库存: " + product.getName());
            }

            // 检查价格是否一致
            if (product.getPrice().compareTo(cartItem.getPrice()) != 0) {
                throw new BusinessException("价格不一致: " + product.getName());
            }

            // 检查产品是否可用
            if (!ProductStatus.ACTIVE.equals(product.getStatus())) {
                throw new BusinessException("商品不可见: " + product.getName());
            }
        }
    }

    private void reduceInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            
            int newStock = product.getStock() - item.getQuantity();
            if (newStock < 0) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
            
            product.setStock(newStock);
            productRepository.update(product);
        }
    }

    public OrderDetailResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // 检查订单是否属于用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("Order does not belong to user");
        }

        Optional<Address> address = addressRepository.findById(order.getAddressId());
        if(address.isEmpty()) {
            throw new BusinessException("没有地址");
        }

        //推荐使用mapper structure
        OrderDetailResponse  orderDetailResponse = new OrderDetailResponse();
        BeanUtils.copyProperties(order, orderDetailResponse);
        List<OrderItemResponse> items = order.getItems().stream().map(orderItem -> {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            BeanUtils.copyProperties(orderItem, orderItemResponse);
            return orderItemResponse;
        }).toList();
        orderDetailResponse.setItems(items);

        AddressResponse  addressResponse = new AddressResponse();
        BeanUtils.copyProperties(address.get(), addressResponse);
        orderDetailResponse.setShippingAddress(addressResponse);
        return orderDetailResponse;
    }

    public List<OrderResponse> getUserOrders(QueryOrderRequest request, Long userId) {
        List<Order> orders = orderRepository.findOrders(userId, request.getStatus());
        return orders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // 验证状态转换合法性
        validateStatusTransition(order.getStatus(), request.getStatus());

        // 更新状态
        order.setStatus(request.getStatus());
        order.setUpdatedAt(LocalDateTime.now());

        // 如果是发货状态，记录跟踪号
        if (Order.OrderStatus.SHIPPED.equals(request.getStatus())) {
            // 这里可以添加跟踪号字段到 Order 模型
            log.info("Order {} shipped with tracking: {}", orderId, request.getTrackingNumber());
        }

        order = orderRepository.update(order);
        log.info("Order {} status updated to {}", orderId, request.getStatus());

        return OrderResponse.fromOrder(order);
    }

    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // 简单的状态机验证
        switch (currentStatus) {
            case PENDING_PAYMENT:
                if (!Order.OrderStatus.PAID.equals(newStatus) && !Order.OrderStatus.CANCELLED.equals(newStatus)) {
                    throw new BusinessException("Invalid status transition from PENDING_PAYMENT to " + newStatus);
                }
                break;
            case PAID:
                if (!Order.OrderStatus.SHIPPED.equals(newStatus) && !Order.OrderStatus.CANCELLED.equals(newStatus)) {
                    throw new BusinessException("Invalid status transition from PAID to " + newStatus);
                }
                break;
            case SHIPPED:
                if (!Order.OrderStatus.DELIVERED.equals(newStatus)) {
                    throw new BusinessException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case DELIVERED:
                if (!Order.OrderStatus.REFUNDED.equals(newStatus)) {
                    throw new BusinessException("Invalid status transition from DELIVERED to " + newStatus);
                }
                break;
            case CANCELLED:
            case REFUNDED:
                throw new BusinessException("Cannot update status of " + currentStatus + " order");
        }
    }

    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // 检查订单是否属于用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("Order does not belong to user");
        }

        // 只能取消待支付状态的订单
        if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BusinessException("Only orders with PENDING_PAYMENT status can be cancelled");
        }

        // 恢复库存
        restoreInventory(order.getItems());

        // 更新订单状态
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.update(order);

        log.info("Order {} cancelled by user {}", orderId, userId);
        return OrderResponse.fromOrder(order);
    }

    private void restoreInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.update(product);
        }
    }

    // 管理员方法
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getRecentOrders(int limit) {
        List<Order> orders = orderRepository.findRecentOrders(limit);
        return orders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }
}