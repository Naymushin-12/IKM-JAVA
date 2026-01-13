package com.recipe.day.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    @Column(name = "title", nullable = false, unique = true, length = 100)
    private String title;
    
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description", length = 500)
    private String description;
    
    // Используем стандартные иконки Font Awesome
    @Column(name = "icon", length = 50)
    private String icon = "fas fa-utensils"; // Значение по умолчанию
    
    // Конструкторы
    public Category() {}
    
    public Category(String title, String description, String icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    @Override
    public String toString() {
        return "Category{id=" + id + ", title='" + title + "'}";
    }
}
