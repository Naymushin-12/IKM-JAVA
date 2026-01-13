package com.recipe.day.repository;

import com.recipe.day.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findAllByOrderByAddedDateDesc();
    
    List<Favorite> findByUserNameContainingIgnoreCaseOrderByAddedDateDesc(String userName);
    
    List<Favorite> findByRecipeIdOrderByAddedDateDesc(Long recipeId);
}