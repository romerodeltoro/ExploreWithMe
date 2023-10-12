package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryDto;

@Mapper
public interface CategoriesMapper {

    CategoriesMapper INSTANCE = Mappers.getMapper(CategoriesMapper.class);

    Category toCategory(CategoryDto categoryDto);

}
