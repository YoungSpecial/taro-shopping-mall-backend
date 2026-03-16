package com.mall.repository;

import com.mall.dto.ProductQueryPageRequest;
import com.mall.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> getProducts(ProductQueryPageRequest request);

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContaining(String keyword);

    List<Product> findByStatus(com.mall.model.ProductStatus status);

    void deleteById(Long id);

    Product update(Product product);

    Boolean existsById(Long id);

    List<Product> findActiveProducts();

    /**
     * Reduce product stock by specified quantity
     * @param productId product ID
     * @param quantity quantity to reduce
     * @return true if successful, false if insufficient stock
     */
    boolean reduceStock(Long productId, int quantity);

    /**
     * Increase product stock by specified quantity
     * @param productId product ID
     * @param quantity quantity to increase
     * @return true if successful
     */
    boolean increaseStock(Long productId, int quantity);

    /**
     * Check if product has sufficient stock
     * @param productId product ID
     * @param quantity required quantity
     * @return true if sufficient stock available
     */
    boolean hasSufficientStock(Long productId, int quantity);

    /**
     * Find products sorted by price (ascending)
     */
    List<Product> findAllByPriceAsc();

    /**
     * Find products sorted by price (descending)
     */
    List<Product> findAllByPriceDesc();

    /**
     * Find products sorted by creation date (newest first)
     */
    List<Product> findAllByNewest();

    /**
     * Find products sorted by popularity (sold quantity)
     */
    List<Product> findAllByPopularity();
}
