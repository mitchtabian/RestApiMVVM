package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;
    private String mRecipeId;
    private boolean mDidRetrieveRecipe;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        mDidRetrieveRecipe = false;
    }

    public LiveData<Recipe> getRecipe(String recipeId){
        mRecipeId = recipeId;
        return mRecipeRepository.getRecipe(recipeId);
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeRepository.isRecipeRequestTimedOut();
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public void setRetrievedRecipe(boolean retrievedRecipe){
        mDidRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return mDidRetrieveRecipe;
    }
}
