package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.service.CategoriesService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesPublicController {

    private final CategoriesService service;

    @GetMapping("/{catId}")
    public ResponseEntity<Category> getCategory(@PathVariable Long catId) {
        return ResponseEntity.ok().body(service.getCategory(catId));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return ResponseEntity.ok().body(service.getAllCategories(from, size));
    }

}
