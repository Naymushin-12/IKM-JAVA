package com.recipe.day.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recipe.day.entity.Recipe;
import com.recipe.day.service.CategoryService;
import com.recipe.day.service.RecipeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/recipes")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Все рецепты
    @GetMapping
    public String listRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        return "recipes/list";
    }
    
    // Форма создания
    @GetMapping("/create")
    public String createRecipeForm(Model model) {
        Recipe recipe = new Recipe();
        recipe.setPreparationTime(30);
        recipe.setDifficulty(3);
        recipe.setServings(4);
        
        model.addAttribute("recipe", recipe);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "recipes/form";
    }
    
    // Форма редактирования
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
    
    // Сохранение (создание или обновление)
    @PostMapping("/save")
    public String saveRecipe(@Valid @ModelAttribute Recipe recipe,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "recipes/form";
        }
        
        // Проверка рейтинга
        if (recipe.getRating() != null) {
            if (recipe.getRating().compareTo(BigDecimal.ZERO) < 0 || 
                recipe.getRating().compareTo(new BigDecimal("5.0")) > 0) {
                result.rejectValue("rating", "error.recipe", "Рейтинг должен быть от 0.0 до 5.0");
                model.addAttribute("categories", categoryService.getAllCategories());
                return "recipes/form";
            }
        }
        
        recipeService.saveRecipe(recipe);
        
        if (recipe.getId() != null) {
            redirectAttributes.addFlashAttribute("success", "Рецепт успешно обновлен");
        } else {
            redirectAttributes.addFlashAttribute("success", "Рецепт успешно добавлен");
        }
        
        return "redirect:/recipes";
    }
    
    // Просмотр рецепта
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
    
    // Удаление рецепта
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
    
    // Поиск рецептов
    @GetMapping("/search")
    public String searchRecipes(@RequestParam("search") String searchTerm, Model model) {
        model.addAttribute("recipes", recipeService.searchRecipesByTitle(searchTerm));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("searchTerm", searchTerm);
        return "recipes/list";
    }
}
