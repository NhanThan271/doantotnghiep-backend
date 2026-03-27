package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Category;
import com.restaurant.doantotnghiep.repository.CategoryRepository;
import com.restaurant.doantotnghiep.service.CategoryService;
import com.restaurant.doantotnghiep.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public Category createCategory(String name, String description, MultipartFile image) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        // Lưu hình ảnh nếu có
        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            category.setImageUrl(imageUrl);
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, String name, String description, MultipartFile image) {
        Category existing = getCategoryById(id);
        existing.setName(name);
        existing.setDescription(description);
        existing.setUpdatedAt(LocalDateTime.now());

        // Nếu có hình mới thì lưu lại và thay thế URL
        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            existing.setImageUrl(imageUrl);
        }

        return categoryRepository.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        Category existing = getCategoryById(id);
        categoryRepository.delete(existing);
    }
}
