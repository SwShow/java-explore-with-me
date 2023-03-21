package ru.practicum.serv.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.serv.category.dto.CategoryDto;
import ru.practicum.serv.category.dto.NewCategoryDto;
import ru.practicum.serv.category.mapper.CategoryMapper;
import ru.practicum.serv.category.model.Category;
import ru.practicum.serv.category.repository.CategoryRepository;
import ru.practicum.serv.event.repository.EventsRepository;
import ru.practicum.serv.exception.ConflictException;
import ru.practicum.serv.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventsRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        Category category = CategoryMapper.INSTANCE.toCategory(categoryDto);
        Category cat;
        try {
            cat = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Имя категории не уникально");
        }
        return CategoryMapper.INSTANCE.toCategoryDto(cat);
    }

    @Transactional
    @Override
    public void deleteCat(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category not found"));
        if (eventRepository.countByCategoryId(catId) > 0) {
            throw new ConflictException("Нельзя удалить категорию с событиями");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto pathCat(Long catId, NewCategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new RuntimeException("Ошибка запроса"));
        category.setName(categoryDto.getName());
        Category cat;
        try {
            cat = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Имя категории не уникально");
        }
        return CategoryMapper.INSTANCE.toCategoryDto(cat);
    }

    @Override
    public Collection<CategoryDto> getAllCategories(Integer from, Integer size) {
        if (categoryRepository.existsById(1L)) {
            Pageable pageable = PageRequest.of(from, size);
            return categoryRepository.findAll(pageable)
                    .stream().map(CategoryMapper.INSTANCE::toCategoryDto)
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                "Категория не найдена"));
        return CategoryMapper.INSTANCE.toCategoryDto(category);

    }
}
