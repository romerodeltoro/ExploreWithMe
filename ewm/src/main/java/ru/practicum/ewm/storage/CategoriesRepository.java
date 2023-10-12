package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.category.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
}
