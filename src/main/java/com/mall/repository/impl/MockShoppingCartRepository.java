package com.mall.repository.impl;

import com.mall.model.ShoppingCart;
import com.mall.repository.ShoppingCartRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MockShoppingCartRepository implements ShoppingCartRepository {

    private final ConcurrentHashMap<Long, ShoppingCart> carts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> userIdToCartId = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        if (cart.getId() == null) {
            cart.setId(idGenerator.getAndIncrement());
            cart.setCreatedAt(LocalDateTime.now());
        }
        cart.setUpdatedAt(LocalDateTime.now());
        carts.put(cart.getId(), cart);
        userIdToCartId.put(cart.getUserId(), cart.getId());
        return cart;
    }

    @Override
    public Optional<ShoppingCart> findById(Long id) {
        return Optional.ofNullable(carts.get(id));
    }

    @Override
    public Optional<ShoppingCart> findByUserId(Long userId) {
        Long cartId = userIdToCartId.get(userId);
        return cartId != null ? Optional.ofNullable(carts.get(cartId)) : Optional.empty();
    }

    @Override
    public ShoppingCart update(ShoppingCart cart) {
        return save(cart);
    }

    @Override
    public void deleteById(Long id) {
        ShoppingCart cart = carts.remove(id);
        if (cart != null) {
            userIdToCartId.remove(cart.getUserId());
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        Long cartId = userIdToCartId.remove(userId);
        if (cartId != null) {
            carts.remove(cartId);
        }
    }

    @Override
    public ShoppingCart createCartForUser(Long userId) {
        ShoppingCart cart = new ShoppingCart(userId);
        return save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Optional<ShoppingCart> cartOpt = findByUserId(userId);
        if (cartOpt.isPresent()) {
            ShoppingCart cart = cartOpt.get();
            cart.getItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());
            save(cart);
        }
    }
}
