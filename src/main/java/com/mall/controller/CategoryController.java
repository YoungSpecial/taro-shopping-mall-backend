package com.mall.controller;

import com.mall.dto.CategoryResponse;
import com.mall.dto.ProductResponse;
import com.mall.model.Category;
import com.mall.model.Product;
import com.mall.repository.CategoryRepository;
import com.mall.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories(
            @RequestParam(required = false) Long parentId) {
        List<Category> categories;
        
        if (parentId != null) {
            categories = categoryRepository.findByParentId(parentId);
        } else {
            categories = categoryRepository.findAll();
        }
        
        List<CategoryResponse> response = categories.stream()
                .map(this::convertToResponse).peek(c -> c.setCount(productRepository.findByCategoryId(c.getId()).size()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return ResponseEntity.ok(convertToResponse(category));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductResponse>> getCategoryProducts(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        List<Product> products = productRepository.findByCategoryId(id);
        
        List<ProductResponse> response = products.stream()
                .map(p -> {
                    ProductResponse productResponse = new ProductResponse();
                    BeanUtils.copyProperties(p, productResponse);
                    return productResponse;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}