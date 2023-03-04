package ru.practicum.serv.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.serv.category.dto.CategoryDto;
import ru.practicum.serv.category.dto.NewCategoryDto;
import ru.practicum.serv.category.model.Category;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category category(NewCategoryDto newCategoryDto);
}
