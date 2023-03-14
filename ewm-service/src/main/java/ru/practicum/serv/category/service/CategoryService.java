package ru.practicum.serv.category.service;

import ru.practicum.serv.category.dto.CategoryDto;
import ru.practicum.serv.category.dto.NewCategoryDto;

public interface CategoryService {
    Object addCategory(CategoryDto category);

    void deleteCat(Long catId);

    Object pathCat(Long catId, NewCategoryDto dto);

    Object getAllCategories(Integer from, Integer size);

    Object getCategoryById(Long catId);
}
