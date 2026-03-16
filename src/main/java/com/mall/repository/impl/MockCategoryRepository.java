package com.mall.repository.impl;

import com.alibaba.fastjson.JSON;
import com.mall.model.Category;
import com.mall.model.Product;
import com.mall.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockCategoryRepository implements CategoryRepository {

    private final ConcurrentHashMap<Long, Category> categories = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        String file = ClassLoader.getSystemResource("mock_categories.json").getFile();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            List<Category> categoryList = JSON.parseArray(sb.toString(), Category.class);
            categories.putAll(categoryList.stream().collect(Collectors.toMap(Category::getId, c -> c)));
        } catch (Exception e) {
            System.err.println("Error loading mock categories: " + e.getMessage());
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
    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idGenerator.getAndIncrement());
            category.setCreatedAt(LocalDateTime.now());
        }
        category.setUpdatedAt(LocalDateTime.now());
        categories.put(category.getId(), category);
        return category;
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(categories.get(id));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return categories.values().stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categories.values().stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        categories.remove(id);
    }

    @Override
    public Category update(Category category) {
        return save(category);
    }

    @Override
    public Boolean existsById(Long id) {
        return categories.containsKey(id);
    }

    @Override
    public List<Category> findRootCategories() {
        return findByParentId(null);
    }
}
