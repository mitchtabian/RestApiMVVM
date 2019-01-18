package com.codingwithmitch.foodrecipes.repositories;

import com.codingwithmitch.foodrecipes.models.Recipe;

public interface RecipeCallback {

    void setRecipe(Recipe recipe);

    void onError(Throwable t);
}
