package com.recipe.day.repository;

import com.recipe.day.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    List<Recipe> findAllByOrderByTitleAsc();
    
    List<Recipe> findByCategoryIdOrderByTitleAsc(Long categoryId);
    
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY r.title")
    List<Recipe> searchByTitle(@Param("title") String title);
    
    @Query("SELECT r FROM Recipe r WHERE r.difficulty = :difficulty ORDER BY r.title")
    List<Recipe> findByDifficulty(@Param("difficulty") Integer difficulty);
    
    @Query("SELECT r FROM Recipe r WHERE r.preparationTime <= :maxTime ORDER BY r.preparationTime ASC, r.title")
    List<Recipe> findByPreparationTimeLessThanEqual(@Param("maxTime") Integer maxTime);
    
    // Нативный запрос для случайного рецепта (оптимально)
    @Query(value = "SELECT * FROM recipes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    List<Recipe> findRandomRecipe();
}