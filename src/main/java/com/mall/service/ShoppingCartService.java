package com.mall.service;

import com.mall.dto.AddToCartRequest;
import com.mall.dto.CartItemResponse;
import com.mall.dto.CartResponse;
import com.mall.dto.UpdateCartItemRequest;
import com.mall.model.CartItem;
import com.mall.model.Product;
import com.mall.model.ProductStatus;
import com.mall.model.ShoppingCart;
import com.mall.repository.ProductRepository;
import com.mall.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    public CartResponse getCartByUserId(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        return convertToResponse(cart);
    }

    public CartResponse addItemToCart(Long userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!productService.hasSufficientStock(product.getId(), request.quantity())) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        ShoppingCart cart = getOrCreateCart(userId);
        
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();
            
            if (!productService.hasSufficientStock(product.getId(), newQuantity)) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setId(System.currentTimeMillis());
            newItem.setCartId(cart.getId());
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setProductImage(product.getImageUrl());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(request.quantity());
            newItem.setCreatedAt(LocalDateTime.now());
            
            cart.getItems().add(newItem);
        }
        
        cart.setUpdatedAt(LocalDateTime.now());
        ShoppingCart updatedCart = shoppingCartRepository.update(cart);
        
        return convertToResponse(updatedCart);
    }

    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        ShoppingCart cart = getOrCreateCart(userId);
        
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (request.quantity() == 0) {
            cart.getItems().removeIf(cartItem -> cartItem.getId().equals(itemId));
        } else {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (!productService.hasSufficientStock(product.getId(), request.quantity())) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            item.setQuantity(request.quantity());
        }
        
        cart.setUpdatedAt(LocalDateTime.now());
        ShoppingCart updatedCart = shoppingCartRepository.update(cart);
        
        return convertToResponse(updatedCart);
    }

    public CartResponse removeItemFromCart(Long userId, Long itemId) {
        ShoppingCart cart = getOrCreateCart(userId);
        
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        
        if (!removed) {
            throw new RuntimeException("Cart item not found");
        }
        
        cart.setUpdatedAt(LocalDateTime.now());
        ShoppingCart updatedCart = shoppingCartRepository.update(cart);
        
        return convertToResponse(updatedCart);
    }

    public CartResponse clearCart(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        
        ShoppingCart updatedCart = shoppingCartRepository.update(cart);
        return convertToResponse(updatedCart);
    }

    private ShoppingCart getOrCreateCart(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> shoppingCartRepository.createCartForUser(userId));
    }

    private CartResponse convertToResponse(ShoppingCart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::convertCartItemToResponse)
                .collect(Collectors.toList());
        
        BigDecimal subtotal = calculateSubtotal(cart);
        int totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        BigDecimal shipping = calculateShipping(cart);
        BigDecimal tax = calculateTax(subtotal);
        BigDecimal discount = calculateDiscount(subtotal);
        BigDecimal total = subtotal.add(shipping).add(tax).subtract(discount);
        
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                itemResponses,
                totalItems,
                subtotal,
                shipping,
                tax,
                discount,
                total,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

    private CartItemResponse convertCartItemToResponse(CartItem item) {
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        
        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductImage(),
                item.getPrice(),
                item.getQuantity(),
                subtotal,
                item.getCreatedAt()
        );
    }

    private BigDecimal calculateSubtotal(ShoppingCart cart) {
        return cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateShipping(ShoppingCart cart) {
        BigDecimal subtotal = calculateSubtotal(cart);
        if (subtotal.compareTo(new BigDecimal("100")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("10");
    }

    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(new BigDecimal("0.08"));
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("50")) >= 0) {
            return subtotal.multiply(new BigDecimal("0.10"));
        }
        return BigDecimal.ZERO;
    }

    public CartValidationResult validateCart(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        List<CartValidationError> errors = new ArrayList<>();
        List<CartItem> validItems = new ArrayList<>();
        
        for (CartItem item : cart.getItems()) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            
            if (productOpt.isEmpty()) {
                errors.add(new CartValidationError(
                        item.getProductId(),
                        "Product not found",
                        CartValidationErrorType.PRODUCT_NOT_FOUND
                ));
                continue;
            }
            
            Product product = productOpt.get();
            
            if (!productService.hasSufficientStock(product.getId(), item.getQuantity())) {
                errors.add(new CartValidationError(
                        item.getProductId(),
                        "Insufficient stock: " + product.getName(),
                        CartValidationErrorType.INSUFFICIENT_STOCK
                ));
            }
            
            if (item.getPrice().compareTo(product.getPrice()) != 0) {
                errors.add(new CartValidationError(
                        item.getProductId(),
                        "Price changed: " + product.getName() + " was " + item.getPrice() + ", now " + product.getPrice(),
                        CartValidationErrorType.PRICE_CHANGED
                ));
                item.setPrice(product.getPrice());
            }
            
            if (product.getStatus() != ProductStatus.ACTIVE) {
                errors.add(new CartValidationError(
                        item.getProductId(),
                        "Product not available: " + product.getName() + " (" + product.getStatus() + ")",
                        CartValidationErrorType.PRODUCT_NOT_AVAILABLE
                ));
            }
            
            validItems.add(item);
        }
        
        if (!errors.isEmpty()) {
            cart.setItems(validItems);
            cart.setUpdatedAt(LocalDateTime.now());
            shoppingCartRepository.update(cart);
        }
        
        return new CartValidationResult(errors.isEmpty(), errors, validItems.size());
    }
    
    public static class CartValidationResult {
        private final boolean valid;
        private final List<CartValidationError> errors;
        private final int validItemCount;
        
        public CartValidationResult(boolean valid, List<CartValidationError> errors, int validItemCount) {
            this.valid = valid;
            this.errors = errors;
            this.validItemCount = validItemCount;
        }
        
        public boolean isValid() { return valid; }
        public List<CartValidationError> getErrors() { return errors; }
        public int getValidItemCount() { return validItemCount; }
    }
    
    public static class CartValidationError {
        private final Long productId;
        private final String message;
        private final CartValidationErrorType errorType;
        
        public CartValidationError(Long productId, String message, CartValidationErrorType errorType) {
            this.productId = productId;
            this.message = message;
            this.errorType = errorType;
        }
        
        public Long getProductId() { return productId; }
        public String getMessage() { return message; }
        public CartValidationErrorType getErrorType() { return errorType; }
    }
    
    public enum CartValidationErrorType {
        PRODUCT_NOT_FOUND,
        INSUFFICIENT_STOCK,
        PRICE_CHANGED,
        PRODUCT_NOT_AVAILABLE
    }
}