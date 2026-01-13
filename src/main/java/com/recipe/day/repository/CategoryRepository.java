package com.recipe.day.repository;

import com.recipe.day.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * Репозиторий для работы с сущностью {@link Category} (категории рецептов).
 * <p>
 * Этот интерфейс предоставляет доступ к операциям с базой данных для сущности Category.
 * Расширяет {@link JpaRepository}, что автоматически предоставляет стандартные
 * CRUD-операции (Create, Read, Update, Delete) без необходимости их реализации.
 * </p>
 * <p>
 * Spring Data JPA автоматически генерирует реализации методов на основе их имен
 * в соответствии с соглашениями об именовании. Это позволяет писать сложные запросы
 * к базе данных без написания SQL или JPQL вручную.
 * </p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Находит все категории, отсортированные по названию в алфавитном порядке (A-Z).
     * <p>
     * Spring Data JPA автоматически генерирует SQL-запрос на основе имени метода:
     * <pre>
     * SELECT * FROM categories ORDER BY title ASC
     * </pre>
     * Этот метод используется для отображения списка всех категорий в пользовательском интерфейсе.
     * </p>
     *
     * @return список всех категорий, отсортированных по названию
     */
    List<Category> findAllByOrderByTitleAsc();
    
    /**
     * Проверяет, существует ли категория с указанным названием.
     * <p>
     * Метод возвращает {@code true}, если категория с таким названием существует,
     * и {@code false} в противном случае.
     * </p>
     * <p>
     * Spring Data JPA генерирует оптимизированный запрос:
     * <pre>
     * SELECT COUNT(*) > 0 FROM categories WHERE title = ?
     * </pre>
     * Этот запрос более эффективен, чем получение полной записи, так как
     * проверяет только существование.
     * </p>
     *
     * @param title название категории для проверки (не должно быть {@code null})
     * @return {@code true} если категория существует, {@code false} в противном случае
     */
    boolean existsByTitle(String title);
}
