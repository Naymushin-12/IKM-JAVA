package com.recipe.day.service;

import com.recipe.day.entity.Recipe;
import com.recipe.day.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAllByOrderByTitleAsc();
    }
    
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }
    
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
    
    public List<Recipe> getRecipesByCategory(Long categoryId) {
        return recipeRepository.findByCategoryIdOrderByTitleAsc(categoryId);
    }
    
    public List<Recipe> searchRecipesByTitle(String title) {
        return recipeRepository.searchByTitle(title);
    }
    
    // Улучшенный метод - используем нативный запрос для случайного рецепта
    public Recipe getRandomRecipe() {
        List<Recipe> randomRecipes = recipeRepository.findRandomRecipe();
        return randomRecipes.isEmpty() ? null : randomRecipes.get(0);
    }
    
    public List<Recipe> getQuickRecipes(Integer maxTime) {
        return recipeRepository.findByPreparationTimeLessThanEqual(maxTime);
    }
}