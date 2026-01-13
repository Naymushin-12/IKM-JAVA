package com.recipe.day.service;

import com.recipe.day.entity.Favorite;
import com.recipe.day.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    public List<Favorite> getAllFavorites() {
        return favoriteRepository.findAllByOrderByAddedDateDesc();
    }
    
    public Optional<Favorite> getFavoriteById(Long id) {
        return favoriteRepository.findById(id);
    }
    
    public Favorite saveFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }
    
    public void deleteFavorite(Long id) {
        favoriteRepository.deleteById(id);
    }
    
    public List<Favorite> getFavoritesByUser(String userName) {
        return favoriteRepository.findByUserNameContainingIgnoreCaseOrderByAddedDateDesc(userName);
    }
    
    public List<Favorite> getFavoritesByRecipe(Long recipeId) {
        return favoriteRepository.findByRecipeIdOrderByAddedDateDesc(recipeId);
    }
    
    public boolean isRecipeInFavorites(Long recipeId, String userName) {
        List<Favorite> favorites = favoriteRepository.findByRecipeIdOrderByAddedDateDesc(recipeId);
        return favorites.stream()
                .anyMatch(f -> f.getUserName().equalsIgnoreCase(userName));
    }
}