package com.recipe.day;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения "Случайный рецепт дня".
 *
 * <p>Приложение предоставляет функционал для управления:</p>
 * <ul>
 *   <li>Категориями рецептов (закуски, основные блюда, десерты и т.д.)</li>
 *   <li>Рецептами с описанием, ингредиентами и инструкциями</li>
 *   <li>Избранными рецептами пользователей</li>
 *   <li>Главной страницей с "Рецептом дня"</li>
 * </ul>
 *
 * <p>Приложение использует:</p>
 * <ul>
 *   <li>Spring Boot 3.x</li>
 *   <li>Spring Data JPA</li>
 *   <li>H2 базу данных</li>
 *   <li>Thymeleaf для HTML шаблонов</li>
 * </ul>
 *
 * @author Приложение "Случайный рецепт дня"
 * @version 1.0
 */
@SpringBootApplication
public class main {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(main.class, args);
    }

}
