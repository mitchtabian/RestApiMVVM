package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecipeListViewModel extends AndroidViewModel
{

    private static final String TAG = "RecipeListViewModel";

    private RecipeRepository mRecipeRepository;

    private boolean mIsViewingRecipes;

    private MediatorLiveData<Boolean> mIsQueryExhausted;
    private MediatorLiveData<Boolean> mIsPerformingQuery;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mIsViewingRecipes = false;

        mRecipeRepository = RecipeRepository.getInstance(application);

        // Prepare the Query Exhausted boolean
        LiveData<Boolean> isQueryExhausted = mRecipeRepository.isQueryExhausted();
        mIsQueryExhausted = new MediatorLiveData<>();

        // assume the query is not exhausted to start with
        mIsQueryExhausted.setValue(false);

        mIsQueryExhausted.addSource(isQueryExhausted, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mIsQueryExhausted.setValue(aBoolean);
            }
        });


        // Prepare the isPerformingQuery boolean
        LiveData<Boolean> isPerformingQuery = mRecipeRepository.isPerformingQuery();
        mIsPerformingQuery = new MediatorLiveData<>();

        // assume the query is not exhausted to start with
        mIsPerformingQuery.setValue(false);

        mIsPerformingQuery.addSource(isPerformingQuery, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mIsPerformingQuery.setValue(aBoolean);
            }
        });

    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeRepository.getRecipes();
    }


    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public LiveData<Boolean> isPerformingQuery(){
        return mIsPerformingQuery;
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
        if(!mIsPerformingQuery.getValue()
                && mIsViewingRecipes
                && !mIsQueryExhausted.getValue()){
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
}



















