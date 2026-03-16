package com.mall.repository;

import com.mall.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    List<Category> findByParentId(Long parentId);

    Optional<Category> findByName(String name);

    void deleteById(Long id);

    Category update(Category category);

    Boolean existsById(Long id);

    List<Category> findRootCategories();
}
