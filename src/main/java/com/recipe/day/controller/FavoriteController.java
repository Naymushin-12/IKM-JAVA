package com.recipe.day.controller;

import com.recipe.day.entity.Favorite;
import com.recipe.day.entity.Recipe;
import com.recipe.day.service.FavoriteService;
import com.recipe.day.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Контроллер для управления избранными рецептами в системе "Случайный рецепт дня".
 * <p>
 * Этот контроллер обрабатывает все операции, связанные с добавлением,
 * просмотром и управлением избранными рецептами. Включает расширенное логирование
 * для отслеживания действий пользователей и диагностики проблем.
 * </p>
 * <p>
 * Контроллер поддерживает создание записей об избранном как для конкретных рецептов,
 * так и с выбором рецепта из списка, а также предоставляет функциональность
 * поиска избранного по имени пользователя.
 * </p>
 */
@Controller
@RequestMapping("/favorites")
public class FavoriteController {
    
    /**
     * Логгер для записи информационных сообщений, предупреждений и ошибок.
     * <p>
     * Используется для:
     * - Отслеживания выполнения операций
     * - Диагностики проблем
     * - Проверка действий пользователей
     * </p>
     */
    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);
    
    /**
     * Сервис для работы с избранными рецептами.
     * <p>
     * Предоставляет доступ к бизнес-логике операций с избранным:
     * создание, чтение, обновление, удаление, поиск и фильтрация.
     * </p>
     */
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * Сервис для работы с рецептами.
     * <p>
     * Используется для получения списка рецептов при создании/редактировании
     * записей об избранном, а также для предварительной проверки наличия рецептов в системе.
     * </p>
     */
    @Autowired
    private RecipeService recipeService;
    
    /**
     * Отображает список избранных рецептов с возможностью фильтрации по пользователю.
     * <p>
     * Обрабатывает GET-запросы к {@code /favorites}. Поддерживает необязательный
     * параметр {@code user} для поиска избранного по имени пользователя.
     * </p>
     * <p>
     * Метод включает расширенное логирование для отслеживания количества
     * доступных рецептов и предупреждения, если рецепты отсутствуют.
     * </p>
     *
     * @param user имя пользователя для фильтрации (может быть {@code null})
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "favorites/list"
     */
    @GetMapping
    public String listFavorites(@RequestParam(value = "user", required = false) String user,
                               Model model) {
        logger.info("GET /favorites - список избранных рецептов");
        List<Favorite> favorites;
        List<Recipe> recipes = recipeService.getAllRecipes(); // Получаем рецепты
        
        logger.info("Количество рецептов в базе: {}", recipes.size());
        if (recipes.isEmpty()) {
            logger.warn("В базе данных нет рецептов! Невозможно добавить в избранное.");
        }
        
        if (user != null && !user.trim().isEmpty()) {
            favorites = favoriteService.getFavoritesByUser(user.trim());
            model.addAttribute("user", user);
            logger.info("Поиск избранного для пользователя: {}", user);
        } else {
            favorites = favoriteService.getAllFavorites();
            logger.info("Получение всех избранных рецептов, найдено: {}", favorites.size());
        }
        
        model.addAttribute("favorites", favorites);
        /** Добавляем рецепты в модель
         */
        model.addAttribute("recipes", recipes);
        return "favorites/list";
    }
    
    /**
     * Отображает форму для создания новой записи об избранном.
     * <p>
     * Обрабатывает GET-запрос к {@code /favorites/create}. Создает новый
     * объект {@link Favorite} с предустановленными значениями:
     * </p>
     * <ul>
     *   <li>Дата добавления: текущее время</li>
     *   <li>Имя пользователя: пустая строка</li>
     * </ul>
     * <p>
     * Метод логирует количество доступных рецептов для выбора.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "favorites/form"
     */
    @GetMapping("/create")
    public String createFavoriteForm(Model model) {
        logger.info("GET /favorites/create - форма создания записи об избранном");
        Favorite favorite = new Favorite();
        favorite.setAddedDate(LocalDateTime.now());
        
        List<Recipe> recipes = recipeService.getAllRecipes();
        logger.info("Доступно рецептов для выбора: {}", recipes.size());
        
        model.addAttribute("favorite", favorite);
        model.addAttribute("recipes", recipes);
        return "favorites/form";
    }
    
    /**
     * Отображает форму для создания записи об избранном для конкретного рецепта.
     * <p>
     * Обрабатывает GET-запрос к {@code /favorites/create/{recipeId}}.
     * Автоматически предварительно выбирает указанный рецепт в форме.
     * </p>
     * <p>
     * Используется, например, когда пользователь хочет добавить рецепт
     * в избранное со страницы просмотра рецепта.
     * </p>
     *
     * @param recipeId идентификатор рецепта, для которого создается запись
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "favorites/form" или перенаправление на "/recipes"
     */
    @GetMapping("/create/{recipeId}")
    public String createFavoriteForRecipe(@PathVariable Long recipeId,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        logger.info("GET /favorites/create/{} - форма создания записи для рецепта", recipeId);
        
        Optional<Recipe> recipe = recipeService.getRecipeById(recipeId);
        if (recipe.isPresent()) {
            logger.info("Рецепт найден: ID={}, Название={}", recipe.get().getId(), recipe.get().getTitle());
            
            Favorite favorite = new Favorite();
            favorite.setRecipe(recipe.get());
            favorite.setAddedDate(LocalDateTime.now());
            
            List<Recipe> recipes = recipeService.getAllRecipes();
            logger.info("Всего рецептов в базе: {}", recipes.size());
            
            model.addAttribute("favorite", favorite);
            model.addAttribute("recipes", recipes);
            return "favorites/form";
        } else {
            logger.warn("Рецепт с ID {} не найден", recipeId);
            redirectAttributes.addFlashAttribute("error", "Рецепт не найден");
            return "redirect:/recipes";
        }
    }
    
    /**
     * Обрабатывает сохранение записи об избранном (создание или обновление).
     * <p>
     * Обрабатывает POST-запрос к {@code /favorites/save}. Выполняет:
     * </p>
     * <ol>
     *   <li>Валидацию данных с использованием аннотаций Bean Validation</li>
     *   <li>Проверку на дублирование (нельзя добавить один рецепт дважды одному пользователю)</li>
     *   <li>Логирование деталей сохраняемой записи</li>
     *   <li>Сохранение записи в базе данных</li>
     *   <li>Перенаправление с сообщением об успехе</li>
     * </ol>
     *
     * @param favorite объект записи об избранном, заполненный из формы
     * @param result объект для хранения ошибок валидации
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "favorites/form" при ошибках или перенаправление на "/favorites"
     */
    @PostMapping("/save")
    public String saveFavorite(@Valid @ModelAttribute Favorite favorite,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        logger.info("POST /favorites/save - сохранение записи об избранном");
        
        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при сохранении записи: {}", result.getAllErrors());
            model.addAttribute("recipes", recipeService.getAllRecipes());
            return "favorites/form";
        }
        
        /** Проверка на дублирование
         */
        if (favorite.getRecipe() != null && favorite.getUserName() != null) {
            boolean alreadyExists = favoriteService.isRecipeInFavorites(
                favorite.getRecipe().getId(), 
                favorite.getUserName()
            );
            
            if (alreadyExists && favorite.getId() == null) {
                result.rejectValue("userName", "error.favorite", 
                    "Вы уже добавили этот рецепт в избранное");
                model.addAttribute("recipes", recipeService.getAllRecipes());
                return "favorites/form";
            }
        }
        
        logger.info("Сохранение записи: рецепт={}, пользователь={}",
                favorite.getRecipe() != null ? favorite.getRecipe().getId() : "null",
                favorite.getUserName());
        
        favoriteService.saveFavorite(favorite);
        redirectAttributes.addFlashAttribute("success", "Рецепт успешно добавлен в избранное");
        return "redirect:/favorites";
    }
    
    /**
     * Удаляет запись об избранном по идентификатору.
     * <p>
     * Обрабатывает GET-запрос к {@code /favorites/delete/{id}}. Удаляет
     * запись из базы данных и логирует операцию.
     * </p>
     * <p>
     * В отличие от других контроллеров, здесь нет предварительной проверки
     * существования записи, так как операция удаления неизменна.
     * </p>
     *
     * @param id идентификатор удаляемой записи
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return перенаправление на "/favorites" с сообщением об успехе
     */
    @GetMapping("/delete/{id}")
    public String deleteFavorite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("GET /favorites/delete/{} - удаление записи об избранном", id);
        favoriteService.deleteFavorite(id);
        redirectAttributes.addFlashAttribute("success", "Рецепт успешно удален из избранного");
        return "redirect:/favorites";
    }
    
    /**
     * Отображает детальную информацию о записи об избранном.
     * <p>
     * Обрабатывает GET-запрос к {@code /favorites/view/{id}}. Находит запись
     * по идентификатору и отображает полную информацию о ней, включая
     * связанный рецепт и комментарий пользователя.
     * </p>
     *
     * @param id идентификатор просматриваемой записи
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "favorites/view" или перенаправление на "/favorites"
     */
    @GetMapping("/view/{id}")
    public String viewFavorite(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("GET /favorites/view/{} - просмотр записи об избранном", id);
        Optional<Favorite> favorite = favoriteService.getFavoriteById(id);
        if (favorite.isPresent()) {
            model.addAttribute("favorite", favorite.get());
            return "favorites/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Запись не найдена");
            return "redirect:/favorites";
        }
    }
    
    /**
     * Отображает форму для редактирования существующей записи об избранном.
     * <p>
     * Обрабатывает GET-запрос к {@code /favorites/edit/{id}}. Находит запись
     * по идентификатору и, если она существует, добавляет ее в модель
     * для предзаполнения формы. Если запись не найдена, перенаправляет
     * на список избранного с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор редактируемой записи
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "favorites/form" или перенаправление на "/favorites"
     */
    @GetMapping("/edit/{id}")
    public String editFavoriteForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("GET /favorites/edit/{} - форма редактирования записи", id);
        Optional<Favorite> favorite = favoriteService.getFavoriteById(id);
        if (favorite.isPresent()) {
            model.addAttribute("favorite", favorite.get());
            model.addAttribute("recipes", recipeService.getAllRecipes());
            return "favorites/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Запись не найдена");
            return "redirect:/favorites";
        }
    }
}