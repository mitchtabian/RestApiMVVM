package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private MutableLiveData<List<Recipe>> mRecipes = new MutableLiveData<>();

    public RecipeListViewModel() {
        
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

}



















