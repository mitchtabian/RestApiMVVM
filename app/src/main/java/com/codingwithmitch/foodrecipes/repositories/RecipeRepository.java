package com.codingwithmitch.foodrecipes.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeCacheClient;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.persistence.RecipeDatabase;
import com.codingwithmitch.foodrecipes.requests.RecipeApiClient;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;



public class RecipeRepository {

    private static final String TAG = "RecipeRepository";

    private static RecipeRepository instance;
    private RecipeDao mRecipeDao;
    private RecipeApiClient mRecipeApiClient;
    private RecipeCacheClient mRecipeCacheClient;

    // RecipeListActivity
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MutableLiveData<Boolean> misPerformingQuery = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes;
    private String mQuery;
    private int mPageNumber;

    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context) {
        mRecipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
        mRecipeApiClient = RecipeApiClient.getInstance();
        mRecipeCacheClient = RecipeCacheClient.getInstance();

        // prepare the mediator
        mRecipes = new MediatorLiveData<>();

        initMediators();
    }

    private void initMediators(){
        // add source for the recipe list API query in RecipeListActivity
        LiveData<List<Recipe>> recipeListApiSource = mRecipeApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null) {
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                } else {
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


    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    public LiveData<Boolean> isQueryExhausted() {
        return mIsQueryExhausted;
    }

    public LiveData<Boolean> isPerformingQuery() {
        return misPerformingQuery;
    }

    public void searchNextPage(){
        searchRecipesApi(mQuery, mPageNumber + 1);
    }

    public void searchRecipesApi(String query, int pageNumber){
        mQuery = query;
        if(pageNumber == 0){
            pageNumber = 1;
        }
        mPageNumber = pageNumber;
        mIsQueryExhausted.setValue(false);
        misPerformingQuery.setValue(true);
        mRecipeApiClient.searchRecipesApi(query, pageNumber);
    }

    private void searchLocalCache(final String query, final int pageNumber){
        Log.d(TAG, "searchLocalCache: searching local cache.");
        mRecipeCacheClient.searchLocalCache(mRecipeDao, query, pageNumber);
    }

    private void doneQuery(List<Recipe> list){
        misPerformingQuery.postValue(false);
        if(list.size() < 30){
            mIsQueryExhausted.postValue(true);
        }
    }

    public LiveData<Recipe> getRecipe(final String recipeId){
        mRecipeApiClient.refreshRecipe(recipeId, mRecipeDao);
        return mRecipeDao.getRecipe(recipeId);
    }

    public void cancelRequest() {
        mRecipeApiClient.cancelRequest();
    }
}
















