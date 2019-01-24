package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;


import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel
{

    private static final String TAG = "RecipeListViewModel";

    private RecipeRepository mRecipeRepository;

    private boolean mIsViewingRecipes;

    private MutableLiveData<Boolean> mIsQueryExhausted;
    private MutableLiveData<Boolean> mIsPerformingQuery;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mIsViewingRecipes = false;
        mRecipeRepository = RecipeRepository.getInstance(application);

        mIsQueryExhausted = new MutableLiveData<>();
        mIsQueryExhausted.setValue(false);
        mIsPerformingQuery = new MutableLiveData<>();
        mIsPerformingQuery.setValue(false);
    }


    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeRepository.getRecipes();
    }

    public LiveData<Boolean> isQueryExhausted(){
        return mRecipeRepository.isQueryExhausted();
    }

    public LiveData<Boolean> isPerformingQuery(){
        return mRecipeRepository.isPerformingQuery();
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void search(String query, int pageNumber){
        mIsViewingRecipes = true;
        mIsQueryExhausted.setValue(false);

        mRecipeRepository.searchRecipesApi(query, pageNumber);
    }

    public void searchNextPage(){
        Log.d(TAG, "searchNextPage: called.");
        if(!isPerformingQuery().getValue()
                && mIsViewingRecipes
                && !isQueryExhausted().getValue()){
            mRecipeRepository.searchNextPage();
        }
    }

    public boolean onBackPressed(){
        if(mIsPerformingQuery.getValue()){
            mRecipeRepository.cancelRequest();
        }
        if(mIsViewingRecipes){
            mIsViewingRecipes = false;
            return false;
        }
        return true;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes){
        mIsViewingRecipes = isViewingRecipes;
    }
}



















