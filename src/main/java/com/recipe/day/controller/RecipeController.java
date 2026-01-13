package com.recipe.day.controller;

import com.recipe.day.entity.Recipe;
import com.recipe.day.service.CategoryService;
import com.recipe.day.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления рецептами в системе "Случайный рецепт дня".
 * <p>
 * Этот контроллер обрабатывает все HTTP-запросы, связанные с операциями CRUD
 * (Создание, Чтение, Обновление, Удаление) для сущности {@link Recipe}.
 * Предоставляет функциональность поиска рецептов по различным критериям
 * и фильтрации по категориям и сложности.
 * </p>
 * <p>
 * Контроллер взаимодействует с пользователем через веб-интерфейс,
 * валидирует входящие данные и делегирует бизнес-логику сервисному слою.
 * </p>
 */
@Controller
@RequestMapping("/recipes")
public class RecipeController {

    /**
     * Сервис для работы с рецептами.
     * <p>
     * Spring автоматически внедряет зависимость, предоставляя доступ
     * ко всем операциям бизнес-логики, связанным с рецептами:
     * поиск, фильтрация, сохранение, удаление и т.д.
     * </p>
     */
    @Autowired
    private RecipeService recipeService;

    /**
     * Сервис для работы с категориями.
     * <p>
     * Используется для получения списка всех категорий при создании/редактировании
     * рецептов, а также для отображения информации о категории при фильтрации.
     * </p>
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * Отображает список рецептов с поддержкой поиска и фильтрации.
     * <p>
     * Обрабатывает GET-запросы к {@code /recipes} и поддерживает три
     * необязательных параметра запроса:
     * </p>
     * <ul>
     * <li>{@code search} - поиск рецептов по названию (частичное совпадение)</li>
     * <li>{@code categoryId} - фильтрация рецептов по идентификатору категории</li>
     * <li>{@code maxTime} - фильтрация рецептов по максимальному времени
     * приготовления</li>
     * </ul>
     * <p>
     * Если ни один параметр не указан, отображаются все рецепты.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "recipes/list"
     */
    @GetMapping
    public String listRecipes(Model model) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        model.addAttribute("recipes", recipes);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "recipes/list";
    }

    /**
     * Отображает форму для создания нового рецепта.
     * <p>
     * Обрабатывает GET-запрос к {@code /recipes/create}. Создает новый
     * объект {@link Recipe} с предустановленными значениями по умолчанию:
     * </p>
     * <ul>
     * <li>Время приготовления: 30 минут</li>
     * <li>Сложность: 3 (средняя)</li>
     * <li>Количество порций: 4</li>
     * <li>Рейтинг: 4.0</li>
     * </ul>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "recipes/form"
     */
    @GetMapping("/create")
    public String createRecipeForm(Model model) {
        Recipe recipe = new Recipe();
        recipe.setPreparationTime(30);
        recipe.setDifficulty(3);
        recipe.setServings(4);
        recipe.setRating(new BigDecimal("4.0"));

        model.addAttribute("recipe", recipe);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "recipes/form";
    }

    /**
     * Отображает форму для редактирования существующего рецепта.
     * <p>
     * Обрабатывает GET-запрос к {@code /recipes/edit/{id}}. Находит рецепт
     * по указанному идентификатору и, если он существует, добавляет его
     * в модель для предзаполнения формы. Если рецепт не найден, перенаправляет
     * на список рецептов с сообщением об ошибке.
     * </p>
     *
     * @param id                 идентификатор редактируемого рецепта
     * @param model              объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "recipes/form" или перенаправление на
     *         "/recipes"
     */
    @GetMapping("/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        if (recipe.isPresent()) {
            model.addAttribute("recipe", recipe.get());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "recipes/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Рецепт не найден");
            return "redirect:/recipes";
        }
    }

    /**
     * Обрабатывает сохранение рецепта (создание или обновление).
     * <p>
     * Обрабатывает POST-запрос к {@code /recipes/save}. Выполняет следующие шаги:
     * </p>
     * <ol>
     * <li>Валидация данных с использованием аннотаций Bean Validation</li>
     * <li>Дополнительная проверка рейтинга (должен быть от 0.0 до 5.0)</li>
     * <li>Сохранение рецепта в базе данных</li>
     * <li>Перенаправление с сообщением об успехе</li>
     * </ol>
     *
     * @param recipe             объект рецепта, заполненный из формы
     * @param result             объект для хранения ошибок валидации
     * @param model              объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "recipes/form" при ошибках или перенаправление
     *         на "/recipes"
     */
    @PostMapping("/save")
    public String saveRecipe(@Valid @ModelAttribute Recipe recipe,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "recipes/form";
        }

        /**
         * Проверка рейтинга
         */
        if (recipe.getRating() != null) {
            if (recipe.getRating().compareTo(new BigDecimal("0.0")) < 0 ||
                    recipe.getRating().compareTo(new BigDecimal("5.0")) > 0) {
                result.rejectValue("rating", "error.recipe", "Рейтинг должен быть от 0.0 до 5.0");
                model.addAttribute("categories", categoryService.getAllCategories());
                return "recipes/form";
            }
        }

        recipeService.saveRecipe(recipe);
        redirectAttributes.addFlashAttribute("success", "Рецепт успешно сохранен");
        return "redirect:/recipes";
    }

    /**
     * Удаляет рецепт по идентификатору.
     * <p>
     * Обрабатывает GET-запрос к {@code /recipes/delete/{id}}. Пытается найти
     * рецепт по идентификатору и, если он существует, удаляет его из базы данных.
     * Если рецепт не найден, возвращается сообщение об ошибке.
     * </p>
     *
     * @param id                 идентификатор удаляемого рецепта
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return перенаправление на "/recipes" с сообщением об успехе или ошибке
     */
    @GetMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        if (recipe.isPresent()) {
            recipeService.deleteRecipe(id);
            redirectAttributes.addFlashAttribute("success", "Рецепт успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("error", "Рецепт не найден");
        }
        return "redirect:/recipes";
    }

    /**
     * Отображает детальную информацию о рецепте.
     * <p>
     * Обрабатывает GET-запрос к {@code /recipes/view/{id}}. Находит рецепт
     * по идентификатору и отображает полную информацию о нем. Если рецепт
     * не найден, перенаправляет на список рецептов с сообщением об ошибке.
     * </p>
     *
     * @param id                 идентификатор просматриваемого рецепта
     * @param model              объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "recipes/view" или перенаправление на
     *         "/recipes"
     */
    @GetMapping("/view/{id}")
    public String viewRecipe(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        if (recipe.isPresent()) {
            model.addAttribute("recipe", recipe.get());
            return "recipes/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Рецепт не найден");
            return "redirect:/recipes";
        }
    }

    /**
     * Выполняет поиск рецептов по названию.
     * <p>
     * Обрабатывает GET-запрос к {@code /recipes/search}. Выполняет
     * регистронезависимый
     * поиск рецептов, в названии которых содержится указанная подстрока.
     * </p>
     *
     * @param searchTerm поисковый запрос
     * @param model      объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "recipes/list"
     */
    @GetMapping("/search")
    public String searchRecipes(@RequestParam("search") String searchTerm, Model model) {
        List<Recipe> recipes = recipeService.searchRecipesByTitle(searchTerm);
        model.addAttribute("recipes", recipes);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("searchTerm", searchTerm);
        return "recipes/list";
    }

    /**
     * Отображает быстрые рецепты (время приготовления <= 30 минут).
     * <p>
     * Обрабатывает GET-запрос к {@code /recipes/quick}. Возвращает рецепты,
     * которые можно приготовить за 30 минут или меньше.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "recipes/list"
     */
    @GetMapping("/quick")
    public String quickRecipes(Model model) {
        List<Recipe> recipes = recipeService.getQuickRecipes(30);
        model.addAttribute("recipes", recipes);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("showQuick", true);
        return "recipes/list";
    }
}
