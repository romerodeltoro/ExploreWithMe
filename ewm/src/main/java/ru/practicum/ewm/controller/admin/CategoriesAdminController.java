package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.service.CategoriesService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoriesAdminController {

    private final CategoriesService categoriesService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid CategoryDto category) {
        return ResponseEntity.status(201).body(categoriesService.createCategory(category));
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Category> updateCategory(
            @RequestBody @Valid CategoryDto category,
            @PathVariable Long catId) {
        return ResponseEntity.ok().body(categoriesService.updateCategory(catId, category));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        categoriesService.deleteCategory(catId);
        return ResponseEntity.status(204).build();
    }

}
