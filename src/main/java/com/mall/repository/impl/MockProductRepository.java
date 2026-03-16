package com.mall.repository.impl;

import com.alibaba.fastjson.JSON;
import com.mall.dto.ProductQueryPageRequest;
import com.mall.model.Product;
import com.mall.model.ProductStatus;
import com.mall.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockProductRepository implements ProductRepository {

    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        String file = ClassLoader.getSystemResource("mock_products.json").getFile();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            List<Product> productList = JSON.parseArray(sb.toString(), Product.class);
            products.putAll(productList.stream().collect(Collectors.toMap(Product::getId, p -> p)));
        } catch (Exception e) {
            System.err.println("Error loading mock products: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<Product> getProducts(ProductQueryPageRequest request) {
        String sort = request.getSortBy();
        List<Product> productList = products.values().stream().toList();
        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    productList =  products.values().stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList());
                    break;
                case "price_desc":
                    productList =  products.values().stream().sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice())).collect(Collectors.toList());
                    break;
                case "newest":
                    productList =  products.values().stream().sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())).collect(Collectors.toList());
                    break;
                case "popular":
                    productList =  products.values().stream().sorted((p1, p2) -> p2.getSold().compareTo(p1.getSold())).collect(Collectors.toList());
                    break;
                case "rating":
                    productList =  products.values().stream().sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating())).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }
        return productList.stream().filter(p -> {
            boolean noCheck = null == request.getCategoryId() && StringUtils.isEmpty(request.getSearch()) && null == request.getMinRating();
            if (noCheck) {
                return true;
            }
            boolean keywordCheck = StringUtils.isEmpty(request.getSearch()) || (StringUtils.isNotEmpty(request.getSearch()) && StringUtils.isNotEmpty(request.getSearch()) && p.getName().contains(request.getSearch()));
            boolean categoryCheck = null == request.getCategoryId() || request.getCategoryId().equals(p.getCategoryId());
            boolean ratingCheck = null == request.getMinRating() || p.getRating() >= request.getMinRating();
            return keywordCheck && categoryCheck && ratingCheck;
        }).collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
            product.setCreatedAt(LocalDateTime.now());
            product.setSold(0);
        }
        product.setUpdatedAt(LocalDateTime.now());
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return products.values().stream()
                .filter(p -> categoryId.equals(p.getCategoryId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByStatus(com.mall.model.ProductStatus status) {
        return products.values().stream()
                .filter(p -> status.equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        products.remove(id);
    }

    @Override
    public Product update(Product product) {
        return save(product);
    }

    @Override
    public Boolean existsById(Long id) {
        return products.containsKey(id);
    }

    @Override
    public List<Product> findActiveProducts() {
        return findByStatus(ProductStatus.ACTIVE);
    }

    @Override
    public boolean reduceStock(Long productId, int quantity) {
        Product product = products.get(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }
        int newStock = product.getStock() - quantity;
        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        if (newStock == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }
        return true;
    }

    @Override
    public boolean increaseStock(Long productId, int quantity) {
        Product product = products.get(productId);
        if (product == null || quantity <= 0) {
            return false;
        }
        int newStock = product.getStock() + quantity;
        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        if (product.getStatus() == ProductStatus.OUT_OF_STOCK && newStock > 0) {
            product.setStatus(ProductStatus.ACTIVE);
        }
        return true;
    }

    @Override
    public boolean hasSufficientStock(Long productId, int quantity) {
        Product product = products.get(productId);
        return product != null && product.getStock() >= quantity;
    }

    @Override
    public List<Product> findAllByPriceAsc() {
        return products.values().stream()
                .sorted((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllByPriceDesc() {
        return products.values().stream()
                .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllByNewest() {
        return products.values().stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllByPopularity() {
        return products.values().stream()
                .sorted((p1, p2) -> p2.getSold().compareTo(p1.getSold()))
                .collect(Collectors.toList());
    }
}
