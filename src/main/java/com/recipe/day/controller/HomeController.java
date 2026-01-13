package com.recipe.day.controller;

import com.recipe.day.entity.Recipe;
import com.recipe.day.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Контроллер для обработки основных страниц приложения.
 * <p>
 * Этот контроллер отвечает за отображение главной страницы приложения
 * с "Рецептом дня" и предоставляет доступ к консоли базы данных H2.
 * Он является точкой входа для пользователей при обращении к корневому URL приложения.
 * </p>
 * <p>
 * Контроллер использует сервисный слой для получения данных о рецептах
 * и передачи их в представление.
 * </p>
 */
@Controller
public class HomeController {
    
    /**
     * Сервис для работы с рецептами.
     * <p>
     * Используется для получения случайного рецепта дня
     * и других операций с рецептами.
     * </p>
     */
    @Autowired
    private RecipeService recipeService;
    
    /**
     * Обрабатывает запрос к корневому URL приложения.
     * <p>
     * Метод отображает главную страницу приложения при обращении
     * к URL {@code http://localhost:8080/}. Получает случайный рецепт
     * через сервисный слой и передает его в представление.
     * </p>
     * <p>
     * Этот метод является точкой входа в веб-приложение для пользователей.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "index"
     */
    @GetMapping("/")
    public String home(Model model) {
        Recipe randomRecipe = recipeService.getRandomRecipe();
        model.addAttribute("recipeOfTheDay", randomRecipe);
        model.addAttribute("hasRecipe", randomRecipe != null);
        return "index";
    }
    
    /**
     * Обрабатывает запрос к URL {@code /index}.
     * <p>
     * Метод предоставляет альтернативный доступ к главной странице
     * по адресу {@code http://localhost:8080/index}. Функционально
     * идентичен методу {@link #home(Model)}, но предоставляет более явный URL.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "index"
     */
    @GetMapping("/index")
    public String index(Model model) {
        return home(model);
    }
    
    /**
     * Перенаправляет на консоль базы данных H2.
     * <p>
     * Метод обрабатывает запрос к URL {@code /h2-test} и выполняет
     * перенаправление на консоль администратора базы данных H2.
     * Используется для быстрого доступа к инструментам администрирования БД
     * во время разработки и отладки приложения.
     * </p>
     * <p>
     * Консоль H2 предоставляет веб-интерфейс для выполнения SQL-запросов,
     * просмотра структуры таблиц и управления данными в базе.
     * </p>
     * @see <a href="http://localhost:8080/h2-console">H2 Console</a>
     */
    @GetMapping("/h2-test")
    public String h2Test() {
        return "redirect:/h2-console";
    }
}
