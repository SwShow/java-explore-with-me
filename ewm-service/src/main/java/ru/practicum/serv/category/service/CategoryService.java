package ru.practicum.serv.category.service;

import ru.practicum.serv.category.dto.CategoryDto;
import ru.practicum.serv.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto category);

    void deleteCat(Long catId);

    CategoryDto pathCat(Long catId, NewCategoryDto dto);

    Collection<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
