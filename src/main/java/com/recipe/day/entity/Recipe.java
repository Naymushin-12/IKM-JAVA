package com.recipe.day.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название рецепта не может быть пустым")
    @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description", length = 500)
    private String description;
    
    @NotNull(message = "Категория обязательна")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @NotNull(message = "Время приготовления обязательно")
    @Min(value = 5, message = "Время приготовления должно быть не менее 5 минут")
    @Max(value = 1440, message = "Время приготовления не должно превышать 24 часа")
    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime;
    
    @NotNull(message = "Сложность обязательна")
    @Min(value = 1, message = "Сложность должна быть не менее 1")
    @Max(value = 5, message = "Сложность не должна превышать 5")
    @Column(name = "difficulty", nullable = false)
    private Integer difficulty;
    
    @DecimalMin(value = "0.0", message = "Рейтинг не может быть меньше 0")
    @DecimalMax(value = "5.0", message = "Рейтинг не может быть больше 5")
    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;
    
    @NotBlank(message = "Ингредиенты не могут быть пустыми")
    @Size(min = 10, max = 2000, message = "Ингредиенты должны быть от 10 до 2000 символов")
    @Column(name = "ingredients", nullable = false, length = 2000)
    private String ingredients;
    
    @NotBlank(message = "Инструкции не могут быть пустыми")
    @Size(min = 20, max = 5000, message = "Инструкции должны быть от 20 до 5000 символов")
    @Column(name = "instructions", nullable = false, length = 5000)
    private String instructions;
    
    @NotNull(message = "Количество порций обязательно")
    @Min(value = 1, message = "Количество порций должно быть не менее 1")
    @Max(value = 20, message = "Количество порций не должно превышать 20")
    @Column(name = "servings", nullable = false)
    private Integer servings;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();
    
    // Конструкторы
    public Recipe() {}
    
    public Recipe(String title, String description, Category category, Integer preparationTime, 
                 Integer difficulty, BigDecimal rating, String ingredients, 
                 String instructions, Integer servings) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.preparationTime = preparationTime;
        this.difficulty = difficulty;
        this.rating = rating;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.servings = servings;
    }
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Integer getPreparationTime() { return preparationTime; }
    public void setPreparationTime(Integer preparationTime) { this.preparationTime = preparationTime; }
    
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }
    
    public List<Favorite> getFavorites() { return favorites; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }
    
    // Вспомогательные методы
    public String getFormattedPreparationTime() {
        if (preparationTime == null) return "";
        int hours = preparationTime / 60;
        int minutes = preparationTime % 60;
        if (hours > 0) {
            return String.format("%d ч %d мин", hours, minutes);
        }
        return String.format("%d мин", minutes);
    }
    
    public String getDifficultyStars() {
        if (difficulty == null) return "";
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < difficulty; i++) stars.append("★");
        for (int i = difficulty; i < 5; i++) stars.append("☆");
        return stars.toString();
    }
    
    @Override
    public String toString() {
        return "Recipe{id=" + id + ", title='" + title + "', category=" + 
               (category != null ? category.getTitle() : "null") + "}";
    }
}