package com.recipe.day.controller;

import com.recipe.day.entity.Category;
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
import java.util.List;
import java.util.Optional;
/**
 * Контроллер для управления категориями рецептов.
 * <p>
 * Этот класс обрабатывает HTTP-запросы, связанные с операциями CRUD (Create, Read, Update, Delete)
 * для сущности {@link Category}. Контроллер взаимодействует с пользователем через веб-интерфейс
 * и делегирует бизнес-логику сервисному слою.
 * </p>
 * <p>
 * Все методы контроллера возвращают имена Thymeleaf шаблонов, которые отображаются пользователю.
 * Контроллер использует паттерн MVC (Model-View-Controller) для разделения ответственности.
 * </p>
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    /**
     * Сервис для работы с категориями.
     * <p>
     * Spring автоматически внедряет зависимость (Dependency Injection)
     * благодаря аннотации {@code @Autowired}. Это позволяет использовать
     * бизнес-логику сервиса без явного создания экземпляра.
     * </p>
     *
     * @see CategoryService
     */
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Сервис для работы с рецептами.
     * <p>
     * Используется для получения рецептов определенной категории
     * при просмотре детальной информации о категории.
     * </p>
     */
    @Autowired
    private RecipeService recipeService;
    
    /**
     * Отображает список всех категорий.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /categories}. Получает все категории
     * из базы данных через сервисный слой и добавляет их в модель для отображения
     * в Thymeleaf шаблоне.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "categories/list"
     */
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }
    
    /**
     * Отображает форму для создания новой категории.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /categories/create}. Создает новый
     * пустой объект {@link Category} и добавляет его в модель для привязки к форме.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "categories/form"
     */
    @GetMapping("/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }
    
    /**
     * Отображает форму для редактирования существующей категории.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /categories/edit/{id}}. Находит категорию
     * по указанному идентификатору и, если она существует, добавляет ее в модель
     * для предзаполнения формы. Если категория не найдена, перенаправляет на список категорий
     * с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор редактируемой категории
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "categories/form" или перенаправление на "/categories"
     */
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categories/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Категория не найдена");
            return "redirect:/categories";
        }
    }
    
    /**
     * Обрабатывает сохранение категории (создание или обновление).
     * <p>
     * Обрабатывает POST-запрос по адресу {@code /categories/save}. Выполняет несколько шагов:
     * </p>
     * <ol>
     *   <li>Валидация данных с использованием аннотаций Bean Validation</li>
     *   <li>Проверка уникальности названия категории</li>
     *   <li>Сохранение категории в базе данных</li>
     *   <li>Перенаправление с сообщением об успехе или ошибке</li>
     * </ol>
     *
     * @param category объект категории, заполненный из формы
     * @param result объект для хранения ошибок валидации
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "categories/form" при ошибках или перенаправление на "/categories"
     */
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute Category category,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        /** Если есть ошибки валидации, возвращаем форму с сообщениями об ошибках
         */
        if (result.hasErrors()) {
            return "categories/form";
        }
        
        /** Проверка на уникальность названия
         */
        if (category.getId() == null) {
            if (categoryService.categoryExists(category.getTitle())) {
                result.rejectValue("title", "error.category", "Категория с таким названием уже существует");
                return "categories/form";
            }
        } else {
            /** Для существующей категории проверяем, изменилось ли название
             */
            Optional<Category> existing = categoryService.getCategoryById(category.getId());
            if (existing.isPresent() &&
                    !existing.get().getTitle().equals(category.getTitle()) &&
                    categoryService.categoryExists(category.getTitle())) {
                result.rejectValue("title", "error.category", "Категория с таким названием уже существует");
                return "categories/form";
            }
        }
        /** Сохранение категории в базе данных
         */
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("success", "Категория успешно сохранена");
        return "redirect:/categories";
    }
    
    /**
     * Удаляет категорию по идентификатору.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /categories/delete/{id}}. Пытается удалить
     * категорию, но только если нет связанных рецептов в этой категории (проверка выполняется
     * в сервисном слое). Если удаление невозможно, возвращается сообщение об ошибке.
     * </p>
     *
     * @param id идентификатор удаляемой категории
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return перенаправление на "/categories" с сообщением об успехе или ошибке
     */
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Категория успешно удалена");
        } else {
            redirectAttributes.addFlashAttribute("error", "Невозможно удалить категорию. Существуют рецепты в этой категории.");
        }
        return "redirect:/categories";
    }
    
    /**
     * Отображает детальную информацию о категории, включая список рецептов этой категории.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /categories/view/{id}}. Находит категорию
     * по идентификатору и все рецепты, принадлежащие этой категории. Если категория не найдена,
     * перенаправляет на список категорий с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор просматриваемой категории
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "categories/view" или перенаправление на "/categories"
     */
    @GetMapping("/view/{id}")
    public String viewCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            
            /** Загрузка рецептов этой категории
             */
            List<Recipe> recipes = recipeService.getRecipesByCategory(id);
            model.addAttribute("recipes", recipes);
            
            return "categories/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Категория не найдена");
            return "redirect:/categories";
        }
    }
}