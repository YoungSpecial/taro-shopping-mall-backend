package com.mall.service;

import com.mall.model.Product;
import com.mall.model.ProductStatus;
import com.mall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public boolean reduceStock(Long productId, int quantity) {
        return productRepository.reduceStock(productId, quantity);
    }

    public boolean increaseStock(Long productId, int quantity) {
        return productRepository.increaseStock(productId, quantity);
    }

    public boolean hasSufficientStock(Long productId, int quantity) {
        return productRepository.hasSufficientStock(productId, quantity);
    }

    public void updateProductStatusBasedOnStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getStock() == 0 && product.getStatus() != ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
            productRepository.update(product);
        } else if (product.getStock() > 0 && product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
            productRepository.update(product);
        }
    }
}