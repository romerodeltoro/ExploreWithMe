package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.CategoriesNotFoundException;
import ru.practicum.ewm.mapper.CategoriesMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.service.CategoriesService;
import ru.practicum.ewm.storage.CategoriesRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;
    @Override
    @Transactional
    public Category createCategory(CategoryDto newCategory) {
        Category category = categoriesRepository.save(CategoriesMapper.INSTANCE.toCategory(newCategory));
        log.info("Создана новая категория - {}", newCategory);
        return category;
    }

    @Override
    public Category getCategory(Long catId) {
        Category category = findCategoryByIdOrElseThrow(catId);
        log.info("Получена категория {}", category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long catId, CategoryDto newCategory) {
        Category category = findCategoryByIdOrElseThrow(catId);
        category.setName(newCategory.getName());
        categoriesRepository.saveAndFlush(category);
        log.info("Категория {} обновлена", category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        findCategoryByIdOrElseThrow(catId);
        categoriesRepository.deleteById(catId);
        log.info("Категория с id '{}' - удалена", catId);
    }

    @Override
    public List<Category> getAllCategories(Integer from, Integer size) {
        log.info("Получен список категорий");
        return categoriesRepository.findAll(PageRequest.of(from, size)).toList();
    }

    private Category findCategoryByIdOrElseThrow(Long id) {
        return categoriesRepository.findById(id).orElseThrow(() -> new CategoriesNotFoundException(
                String.format("Категории с id %d нет в базе", id)
        ));
    }
}
