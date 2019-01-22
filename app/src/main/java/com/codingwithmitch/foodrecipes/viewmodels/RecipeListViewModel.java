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
    private boolean mIsPerformingQuery;
    private boolean mIsViewingRecipes;

    private MediatorLiveData<Boolean> mIsQueryExhausted;
    private final MediatorLiveData<List<Recipe>> mRecipes;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
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


        // Prepare the Recipe List Data Observer
        LiveData<List<Recipe>> observableRecipes = mRecipeRepository.getRecipes();
        mRecipes = new MediatorLiveData<>();

        // set by default null, until we get data from the web service.
        mRecipes.setValue(null);

        // observe the changes of the Recipes list from the web server and forward them to UI
        mRecipes.addSource(observableRecipes, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mIsPerformingQuery = false;
                mRecipes.setValue(recipes);
            }
        });
    }


    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void displaySearchCategories(){
        mIsViewingRecipes = false;
        List<Recipe> categories = new ArrayList<>();
        for(int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes.setValue(categories);
    }

    private void displayLoadingScreen(){
        Recipe recipe = new Recipe();
        recipe.setTitle("LOADING...");
        List<Recipe> loadingList = new ArrayList<>();
        loadingList.add(recipe);
        mRecipes.setValue(loadingList);
    }

    public void searchNextPage(){
        if(!mIsPerformingQuery
                && !mIsQueryExhausted.getValue()
                && mIsViewingRecipes){
            mRecipeRepository.searchNextPage();
        }
    }

    public void search(String query, int pageNumber){
        displayLoadingScreen();
        mIsPerformingQuery = true;
        mIsViewingRecipes = true;
        mRecipeRepository.searchApi(query, pageNumber);
    }

    public Recipe getSelectedRecipe(int position){
        if(mRecipes.getValue().size() > 0){
            return mRecipes.getValue().get(position);
        }
        return null;
    }

    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            mRecipeRepository.cancelQuery();
            displaySearchCategories();
        }
        else{
            if(mIsViewingRecipes){
                displaySearchCategories();
            }else{
                return true; // press back btn
            }
        }
        return false;
    }
}



















