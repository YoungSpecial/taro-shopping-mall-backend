package com.mall.repository;

import com.mall.model.ShoppingCart;

import java.util.Optional;

public interface ShoppingCartRepository {

    ShoppingCart save(ShoppingCart cart);

    Optional<ShoppingCart> findById(Long id);

    Optional<ShoppingCart> findByUserId(Long userId);

    ShoppingCart update(ShoppingCart cart);

    void deleteById(Long id);

    void deleteByUserId(Long userId);

    ShoppingCart createCartForUser(Long userId);

    void clearCart(Long userId);
}
