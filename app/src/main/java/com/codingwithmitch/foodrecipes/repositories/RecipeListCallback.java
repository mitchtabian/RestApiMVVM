package com.codingwithmitch.foodrecipes.repositories;

import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

public interface RecipeListCallback {

    void setRecipes(List<Recipe> recipes);

    void onQueryStart();

    void onQueryDone();
}
