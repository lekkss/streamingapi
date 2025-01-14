package com.lekkss.streamingapi.services.impl;

import com.lekkss.streamingapi.models.Category;
import com.lekkss.streamingapi.repositories.CategoryRepository;
import com.lekkss.streamingapi.services.CategoryService;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Response<?> addCategory(Category category) {
        // Validate the input to ensure it's not null or empty
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return new Response<>(false, "Category name cannot be empty", null);
        }
        // Check if the category already exists
        if (categoryRepository.findByName(category.getName().trim()).isPresent()) {
            return new Response<>(false, "Category with this name already exists", null);
        }
        // Create and save the new category
//        Category category = new Category();
//        category.setName(name.trim());
        categoryRepository.save(category);

        // Return a success response with the saved category
        return new Response<>(true, "Category added successfully", category);
    }


    @Override
    public Response<?> updateCategory(Category category, Integer id) {
        // Check if the category exists by ID
        if (categoryRepository.existsById(id)) {
            // Fetch the existing category
            Category existingCategory = categoryRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Category with ID " + id + " not found.")
            );

            // Update the fields
            existingCategory.setName(category.getName());
            existingCategory.setUpdatedAt(LocalDateTime.now());

            // Save the updated category
            categoryRepository.save(existingCategory);

            return new Response<>(true, "Category updated successfully", existingCategory);
        }
        // If the category does not exist, return an appropriate response
        return new Response<>(false, "Category with ID " + id + " does not exist", null);
    }


    @Override
    public Response<?> deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return new Response<>(true, "Category deleted successfully", null);
        }
        return new Response<>(true, "Category not found", null);
    }

    @Override
    public Response<?> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return  new Response<>(true, "Categories retrieved successfully", categories);
    }
}
