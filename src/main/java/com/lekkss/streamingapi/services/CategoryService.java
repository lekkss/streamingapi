package com.lekkss.streamingapi.services;

import com.lekkss.streamingapi.models.Category;
import com.lekkss.streamingapi.utils.Response;

public interface CategoryService {
    Response<?> addCategory(Category category);
    Response<?> updateCategory(Category category, Integer id);
    Response<?> deleteCategory(Integer id);
    Response<?> getAllCategories();
}
