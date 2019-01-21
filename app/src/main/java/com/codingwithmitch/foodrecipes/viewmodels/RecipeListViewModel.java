package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeListCallback;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RecipeListViewModel extends AndroidViewModel implements
        RecipeListCallback
{

    private RecipeRepository mRecipeRepository;
    private MutableLiveData<List<Recipe>> mRecipes = new MutableLiveData<>();
    private boolean mIsPerformingQuery;
    private boolean mIsViewingRecipes;
    private boolean mIsQueryExhausted;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        mRecipeRepository.setRecipeListCallback(this);
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public boolean getIsViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes) {
        this.mIsViewingRecipes = isViewingRecipes;
    }

    public boolean getIsPerformingQuery(){
        return mIsPerformingQuery;
    }

    public void cancelQuery(){
        mRecipeRepository.onCancel();
    }

    public boolean getIsQueryExhausted(){
        return mIsQueryExhausted;
    }

    @Override
    public void setRecipes(List<Recipe> recipes) {

        // 1) make http request using Retrofit to retrieve the data
        // 2) use a callback method to send the list of recipes back
        // 3) call mRecipes.setValue(recipes);
        // 4) Any observers in RecipeListActivity will be automatically updated

        mRecipes.setValue(recipes);
    }

    @Override
    public void appendRecipes(List<Recipe> recipes) {
        List<Recipe> currentRecipes = mRecipes.getValue();
        currentRecipes.addAll(recipes);
        mRecipes.setValue(currentRecipes);
    }

    @Override
    public void onQueryExhausted() {
        setQueryExhausted();
    }

    private void setQueryExhausted(){
        mIsQueryExhausted = true;
        List<Recipe> currentRecipes = mRecipes.getValue();
        Recipe exhaustedMarkerRecipe = new Recipe();
        exhaustedMarkerRecipe.setTitle("EXHAUSTED...");
        currentRecipes.add(exhaustedMarkerRecipe);
        mRecipes.setValue(currentRecipes);
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
        if(!mIsPerformingQuery && getIsViewingRecipes()){
            mRecipeRepository.searchNextPage();
        }
    }

    public void search(String query, int pageNumber){
        displayLoadingScreen();
        mRecipeRepository.searchApi(query, pageNumber);
    }


    public Recipe getSelectedRecipe(int position){
        if(mRecipes.getValue().size() > 0){
            return mRecipes.getValue().get(position);
        }
        return null;
    }

    @Override
    public void onQueryStart(){
        mIsPerformingQuery = true;
    }

    @Override
    public void onQueryDone(){
        mIsPerformingQuery = false;
    }
}



















