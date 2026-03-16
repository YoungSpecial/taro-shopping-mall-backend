package com.mall.service;

import com.mall.model.Product;
import com.mall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductStatusScheduler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Scheduled(fixedRate = 300000)
    public void updateProductStatusesBasedOnStock() {
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            productService.updateProductStatusBasedOnStock(product.getId());
        }
    }
}