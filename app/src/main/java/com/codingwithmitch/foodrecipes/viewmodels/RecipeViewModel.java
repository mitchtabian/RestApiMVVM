package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeCallback;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends AndroidViewModel implements RecipeCallback {

    private MutableLiveData<Throwable> mQueryError = new MutableLiveData<>();
    private MutableLiveData<Recipe> mRecipe  = new MutableLiveData<>();
    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        mRecipeRepository.setRecipeCallback(this);
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public LiveData<Throwable> getQueryError(){
        return mQueryError;
    }

    public void search(String recipeId) {
        mRecipeRepository.searchForRecipe(recipeId);
    }

    @Override
    public void setRecipe(Recipe recipe) {
        mRecipe.setValue(recipe);
    }

    @Override
    public void onError(Throwable t) {
        mQueryError.setValue(t);
    }
}


















