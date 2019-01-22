package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends AndroidViewModel {

    private MediatorLiveData<String> mErrorMessage;
    private MediatorLiveData<Recipe> mRecipe;
    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);

        mRecipe = new MediatorLiveData<>();
        mRecipe.setValue(null);
        LiveData<Recipe> recipe = mRecipeRepository.getRecipe();
        mRecipe.addSource(recipe, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                mRecipe.setValue(recipe);
            }
        });

        mErrorMessage = new MediatorLiveData<>();
        mErrorMessage.setValue(null);
        LiveData<String> errorMessage = mRecipeRepository.getRecipeQueryError();
        mErrorMessage.addSource(errorMessage, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMessage) {
                mErrorMessage.setValue(errorMessage);
            }
        });
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public LiveData<String> getQueryError(){
        return mErrorMessage;
    }

    public void search(String recipeId) {
        mRecipeRepository.searchForRecipe(recipeId);
    }
}


















