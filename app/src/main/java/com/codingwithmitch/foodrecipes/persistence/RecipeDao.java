package com.codingwithmitch.foodrecipes.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {

    @Insert(onConflict = REPLACE)
    void insertRecipes(Recipe... recipes);

    // pageNumber - 1 b/c the api starts at page 1. But local db starts at page 0
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY social_rank DESC LIMIT 30 OFFSET ((:pageNumber - 1) * 30)")
    List<Recipe> searchRecipes(String query, int pageNumber);

    @Query("SELECT * FROM recipes")
    List<Recipe> getRecipes();

    @Query("SELECT * FROM recipes WHERE recipe_id = :recipeId")
    LiveData<Recipe> getRecipe(String recipeId);


}
