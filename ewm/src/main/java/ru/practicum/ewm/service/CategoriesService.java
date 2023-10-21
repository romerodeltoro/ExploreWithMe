package ru.practicum.ewm.service;

import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryDto;

import java.util.List;

public interface CategoriesService {
    Category createCategory(CategoryDto newCategory);

    Category updateCategory(Long catId, CategoryDto category);

    void deleteCategory(Long catId);

    Category getCategory(Long catId);

    List<Category> getAllCategories(Integer from, Integer size);
}
