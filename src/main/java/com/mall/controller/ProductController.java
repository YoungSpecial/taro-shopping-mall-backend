package com.mall.controller;

import com.mall.dto.PaginatedResponse;
import com.mall.dto.ProductQueryPageRequest;
import com.mall.dto.ProductResponse;
import com.mall.model.Product;
import com.mall.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<PaginatedResponse<ProductResponse>> searchProducts(@RequestBody ProductQueryPageRequest request) {
        List<Product> products = productRepository.getProducts(request);

        int page = request.getPage() > 0 ? request.getPage() : 1;
        int size = request.getSize() > 0 ? request.getSize() : 10;
        int totalElements = products.size();
        int start = Math.min((page - 1) * size, totalElements);
        int end = Math.min(start + size, totalElements);
        List<Product> paginatedProducts = products.subList(start, end);

        List<ProductResponse> content = paginatedProducts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PaginatedResponse<ProductResponse> response = PaginatedResponse.of(
                content, page, size, totalElements
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(convertToResponse(product));
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        return response;
    }
}