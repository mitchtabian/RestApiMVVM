package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends AndroidViewModel {

    private MutableLiveData<Recipe> mRecipe  = new MutableLiveData<>();
    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }
}














