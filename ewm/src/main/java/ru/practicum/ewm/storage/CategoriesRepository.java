package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.category.Category;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {
}
