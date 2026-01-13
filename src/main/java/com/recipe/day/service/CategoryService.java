package com.recipe.day.service;

import com.recipe.day.entity.Category;
import com.recipe.day.repository.CategoryRepository;
import com.recipe.day.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 * Сервис для управления операциями с категориями рецептов.
 * <p>
 * Этот класс предоставляет бизнес-логику для работы с категориями, включая
 * создание, чтение, обновление и удаление (CRUD) категорий.
 * Он действует как промежуточный слой между контроллерами и репозиториями.
 * </p>
 * <p>
 * <strong>Роль в архитектуре приложения:</strong>
 * <ul>
 *   <li>Контроллер → Сервис → Репозиторий → База данных</li>
 *   <li>Содержит бизнес-логику и правила валидации</li>
 *   <li>Управляет транзакциями</li>
 *   <li>Обрабатывает исключения</li>
 * </ul>
 * </p>
 */
@Service
public class CategoryService {
    
    /**
     * Репозиторий для работы с категориями в базе данных.
     * <p>
     * Spring автоматически внедряет реализацию этого интерфейса,
     * сгенерированную Spring Data JPA.
     * </p>
     */
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Репозиторий для работы с рецептами.
     * <p>
     * Используется для проверки наличия рецептов в категории
     * перед ее удалением.
     * </p>
     */
    @Autowired
    private RecipeRepository recipeRepository;
    
    /**
     * Возвращает список всех категорий, отсортированных по названию.
     * <p>
     * Метод делегирует выполнение запроса репозиторию, который
     * использует Spring Data JPA для генерации SQL-запроса.
     * </p>
     *
     * @return список всех категорий в алфавитном порядке по названию
     * @apiNote SQL: SELECT * FROM categories ORDER BY title ASC
     */
    public List<Category> getAllCategories() {
        /** Делегируем запрос репозиторию
         */
        return categoryRepository.findAllByOrderByTitleAsc();
    }
    
    /**
     * Находит категорию по ее уникальному идентификатору.
     * <p>
     * Возвращает {@link Optional}, который может быть пустым,
     * если категория с указанным ID не найдена.
     * </p>
     *
     * @param id идентификатор категории для поиска
     * @return {@link Optional} содержащий найденную категорию или пустой
     * @throws IllegalArgumentException если id равен null
     * @apiNote SQL: SELECT * FROM categories WHERE id = ?
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Сохраняет категорию в базе данных.
     * <p>
     * Если категория уже существует (имеет id), выполняется обновление.
     * Если категория новая (id равен null), выполняется вставка.
     * </p>
     *
     * @param category объект категории для сохранения
     * @return сохраненная категория (с присвоенным id, если была новой)
     * @throws IllegalArgumentException если category равен null
     * @apiNote SQL: INSERT INTO categories ... или UPDATE categories ...
     */
    public Category saveCategory(Category category) {
        /** JPA автоматически определяет: insert или update
         */
        return categoryRepository.save(category);
    }
    
    /**
     * Удаляет категорию по идентификатору, если в ней нет рецептов.
     * <p>
     * Метод выполняется в транзакции для обеспечения атомарности операции.
     * Сначала проверяется существование категории, затем наличие рецептов в этой категории.
     * </p>
     *
     * @param id идентификатор категории для удаления
     * @return {@code true} если категория была удалена, {@code false} если удаление невозможно
     * @apiNote Логика:
     *          1. Проверить существование категории
     *          2. Проверить наличие рецептов в категории
     *          3. Удалить категорию, если рецептов нет
     * @transactional Гарантирует, что операция выполнится полностью или не выполнится вообще
     */
    @Transactional
    public boolean deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            /** Проверяем, есть ли рецепты в этой категории
             */
            List<com.recipe.day.entity.Recipe> recipes = recipeRepository.findByCategoryIdOrderByTitleAsc(id);
            if (recipes.isEmpty()) {
                categoryRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Проверяет существование категории с указанным названием.
     * <p>
     * Используется для валидации уникальности названия категории
     * перед созданием нового или обновлением существующего.
     * </p>
     *
     * @param title название категории для проверки
     * @return {@code true} если категория с таким названием уже существует,
     *         {@code false} в противном случае
     * @throws IllegalArgumentException если title равен null или пустой
     * @apiNote SQL: SELECT COUNT(*) > 0 FROM categories WHERE title = ?
     */
    public boolean categoryExists(String title) {
        return categoryRepository.existsByTitle(title);
    }
}
