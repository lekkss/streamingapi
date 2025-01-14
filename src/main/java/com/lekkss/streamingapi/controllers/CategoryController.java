package com.lekkss.streamingapi.controllers;

import com.lekkss.streamingapi.models.Category;
import com.lekkss.streamingapi.services.CategoryService;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Response<?>> getCategories() {
        Response<?> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Response<?>> addCategory(@RequestBody Category request) {
        Response<?> category = categoryService.addCategory(request);
        return ResponseEntity.ok(category);
    }

    @PutMapping("{id}")
    public ResponseEntity<Response<?>> updateCategory(@PathVariable("id") Integer id, @RequestBody Category request) {
        Response<?> category = categoryService.updateCategory(request, id);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Response<?>> deleteCategory(@PathVariable Integer id) {
        Response<?> category = categoryService.deleteCategory(id);
        return ResponseEntity.ok(category);
    }
}
