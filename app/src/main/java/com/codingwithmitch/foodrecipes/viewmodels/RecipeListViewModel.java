package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;
    private MutableLiveData<List<Recipe>> mRecipes = new MutableLiveData<>();

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public void setRecipes(List<Recipe> recipes) {

        // 1) make http request using Retrofit to retrieve the data
        // 2) use a callback method to send the list of recipes back
        // 3) call mRecipes.setValue(recipes);
        // 4) Any observers in RecipeListActivity will be automatically updated

        mRecipes.setValue(recipes);
    }


    public void search(String query, int pageNumber){
        mRecipeRepository.searchApi(query, pageNumber);
    }
}



















