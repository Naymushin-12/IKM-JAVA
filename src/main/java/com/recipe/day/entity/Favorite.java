package com.recipe.day.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Класс-сущность, представляющий избранный рецепт в системе "Случайный рецепт дня".
 * <p>
 * Отображается на таблицу "favorites" в базе данных. Каждая запись связана с конкретным рецептом
 * и представляет факт добавления рецепта в избранное пользователем.
 * </p>
 */
@Entity
@Table(name = "favorites")
public class Favorite {
    /**
     * Уникальный идентификатор записи об избранном.
     * <p>
     * Первичный ключ таблицы, генерируется автоматически базой данных
     * при вставке новой записи.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Рецепт, добавленный в избранное. Обязательное поле.
     * <p>
     * Связь Many-to-One с сущностью Recipe. Многие записи об избранном
     * могут ссылаться на один рецепт.
     * </p>
     */
    @NotNull(message = "Рецепт обязателен")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    /**
     * Имя пользователя, добавившего рецепт в избранное.
     * <p>
     * Обязательное поле, длина от 1 до 100 символов.
     * В реальном приложении здесь мог бы быть ID пользователя
     * из системы аутентификации.
     * </p>
     */
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 1, max = 100, message = "Имя должно быть от 1 до 100 символов")
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;
    
    /**
     * Комментарий пользователя к избранному рецепту.
     * <p>
     * Необязательное поле, может содержать заметки пользователя
     * о том, почему рецепт добавлен в избранное.
     * </p>
     */
    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    @Column(name = "comment", length = 500)
    private String comment;
    
    /**
     * Дата и время добавления рецепта в избранное.
     * <p>
     * Заполняется автоматически текущей датой и временем
     * при создании записи.
     * </p>
     */
    @Column(name = "added_date")
    private LocalDateTime addedDate;
    
    /**
     * Конструктор по умолчанию.
     * <p>
     * Автоматически устанавливает текущую дату добавления.
     * Используется JPA для создания экземпляров сущности.
     * </p>
     */
    public Favorite() {
        this.addedDate = LocalDateTime.now();
    }
    
    /**
     * Конструктор с параметрами для удобного создания объектов.
     * <p>
     * Использует setRecipe() для установки двусторонней связи.
     * </p>
     *
     * @param recipe рецепт для добавления в избранное
     * @param userName имя пользователя
     * @param comment комментарий пользователя
     */
    public Favorite(Recipe recipe, String userName, String comment) {
        setRecipe(recipe); // Используем наш setter для установки связи
        this.userName = userName;
        this.comment = comment;
        this.addedDate = LocalDateTime.now();
    }
    
    /** Геттеры и сеттеры
     */
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    /**
     * Сеттер для установки рецепта с управлением двусторонней связью.
     * <p>
     * Удаляет запись из старого рецепта и добавляет в новый.
     * </p>
     *
     * @param recipe новый рецепт для связи
     */
    public void setRecipe(Recipe recipe) {
        /** Если устанавливается тот же самый рецепт, ничего не делаем
         */
        if (this.recipe != null && this.recipe.equals(recipe)) {
            return;
        }
        
        /** Удаляем себя из старого рецепта
         */
        if (this.recipe != null) {
            Recipe oldRecipe = this.recipe;
            this.recipe = null;
            oldRecipe.getFavorites().remove(this);
        }
        
        /** Устанавливаем новый рецепт
         */
        this.recipe = recipe;
        
        /** Добавляем себя в новый рецепт
         */
        if (recipe != null && !recipe.getFavorites().contains(this)) {
            recipe.getFavorites().add(this);
        }
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
    
    /**
     * Возвращает дату добавления в читаемом формате (заменяет 'T' на пробел).
     *
     * @return отформатированная дата или пустая строка, если дата не установлена
     */
    public String getFormattedDate() {
        if (addedDate == null) return "";
        return addedDate.toString().replace('T', ' ');
    }
    
    /**
     * Возвращает строковое представление объекта Favorite.
     * <p>
     * Используется для отладки, логирования и удобного вывода в консоль.
     * Пример вывода: "Favorite{id=1, recipe='Паста Карбонара', userName='Иван', addedDate=2024-01-10T18:30}"
     * </p>
     *
     * @return строку с основными полями записи об избранном
     */
    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", recipe=" + (recipe != null ? recipe.getTitle() : "null") +
                ", userName='" + userName + '\'' +
                ", comment='" + comment + '\'' +
                ", addedDate=" + addedDate +
                '}';
    }
}