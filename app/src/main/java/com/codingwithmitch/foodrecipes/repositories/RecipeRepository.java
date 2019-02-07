package com.codingwithmitch.foodrecipes.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeCacheClient;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.persistence.RecipeDatabase;
import com.codingwithmitch.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private RecipeCacheClient mRecipeCacheClient;
    private RecipeDao mRecipeDao;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();

    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context){
        mRecipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
        mRecipeApiClient = RecipeApiClient.getInstance();
        mRecipeCacheClient = RecipeCacheClient.getInstance();
        initMediators();
    }

    private void initMediators(){
        // add source for the recipe list API query in RecipeListActivity
        LiveData<List<Recipe>> recipeListApiSource = mRecipeApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if(recipes != null){
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                }
                else{
                    // search database cache
//                    doneQuery(null);
                    searchLocalCache(mQuery, mPageNumber);
                }
            }
        });

        // add source for the recipe list CACHE query in RecipeListActivity
        LiveData<List<Recipe>> recipeListCacheSource = mRecipeCacheClient.getRecipes();
        mRecipes.addSource(recipeListCacheSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null) {
                    mRecipes.setValue(recipes);
                }
                doneQuery(recipes); // can't be null from cache. It will return empty list
            }
        });
    }

    private void doneQuery(List<Recipe> list){
        if(list != null){
            if (list.size() % 30 != 0) {
                mIsQueryExhausted.setValue(true);
            }
        }
        else{
            mIsQueryExhausted.setValue(true);
        }
    }

    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    // Room db is SINGLE SOURCE OF TRUTH for single Recipe
    public LiveData<Recipe> getRecipe(final String recipeId){
        mRecipeApiClient.searchRecipeById(recipeId, mRecipeDao); // Refresh the recipe in room db
        return mRecipeDao.getRecipe(recipeId); // show the recipe from room db
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }


    public void searchRecipesApi(String query, int pageNumber){
        if(pageNumber == 0){
            pageNumber = 1;
        }
        mQuery = query;
        mPageNumber = pageNumber;
        mIsQueryExhausted.setValue(false);
        mRecipeApiClient.searchRecipesApi(query, pageNumber);
    }

    public void searchNextPage(){
        searchRecipesApi(mQuery, mPageNumber + 1);
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }



    private void searchLocalCache(final String query, final int pageNumber){
        mRecipeCacheClient.searchLocalCache(mRecipeDao, query, pageNumber);
    }
}











