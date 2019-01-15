package com.codingwithmitch.restapimvvm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.codingwithmitch.restapimvvm.MyApplication;
import com.codingwithmitch.restapimvvm.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.restapimvvm.models.Recipe;
import com.codingwithmitch.restapimvvm.requests.RecipeApi;
import com.codingwithmitch.restapimvvm.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.restapimvvm.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListViewModel extends AndroidViewModel implements RecipeRecyclerAdapter.OnRecipeListener{

    private static final String TAG = "RecipeListViewModel";

    private MutableLiveData<List<Recipe>> mRecipes;
    private MutableLiveData<Recipe> mSelectedRecipe;
    private MutableLiveData<Boolean> mIsPerformingQuery;
    private RecipeApi mRecipeApi;
    private String mQuery = "";
    private int mPageNumber = 0;
    private boolean mIsQueryExhausted;
    private boolean mIsViewingCategories;


    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeApi = ((MyApplication) application).getRetrofit().create(RecipeApi.class);
        mRecipes = new MutableLiveData<>();
        mSelectedRecipe = new MutableLiveData<>();
        mIsPerformingQuery = new MutableLiveData<>();
        mIsPerformingQuery.setValue(false);
        mIsQueryExhausted = false;
        mIsViewingCategories = false;
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    public LiveData<Recipe> getSelectedRecipe(){
        return mSelectedRecipe;
    }

    public LiveData<Boolean> getIsPerformingQuery(){
        return mIsPerformingQuery;
    }

    public Boolean getIsViewingCategories(){
        return mIsViewingCategories;
    }

    public void searchNextPage(){
        if(!mIsPerformingQuery.getValue() && !mIsViewingCategories){
            search(mQuery, mPageNumber + 1);
        }
    }

    public void setSearchCategories(){
        mIsViewingCategories = true;
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

    private void setLoadingScreen(){
        Recipe recipe = new Recipe();
        recipe.setTitle("LOADING...");
        List<Recipe> loadingList = new ArrayList<>();
        loadingList.add(recipe);
        mRecipes.setValue(loadingList);
    }

    public void search(String query, int pageNumber){
        mIsPerformingQuery.setValue(true);

        if(pageNumber == 0){
            pageNumber = 1;
            mIsQueryExhausted = false;
        }
        if(pageNumber == 1){
            setLoadingScreen();
        }

        mQuery = query;
        mPageNumber = pageNumber;

        if(mIsQueryExhausted){
            Log.e(TAG, "search: no more results for this query.");
        }
        else{
            // Do search using Retrofit
            Call<RecipeSearchResponse> responseCall = mRecipeApi
                    .searchRecipe(
                            Constants.API_KEY,
                            query,
                            String.valueOf(mPageNumber)
                    );

            responseCall.enqueue(new Callback<RecipeSearchResponse>() {
                @Override
                public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                    Log.d(TAG, "onResponse: Server Response: " + response.toString());
                    if(response.code() == 200){
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        if(response.body().getCount() == 0){
                            mIsQueryExhausted = true;
                        }
                    }
                    else {
                        try {
                            Log.d(TAG, "onResponse: " + response.errorBody().string());
                            mIsQueryExhausted = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Set results to mRecipes list
                    try{
                        if(mPageNumber == 1){
                            mRecipes.setValue(response.body().getRecipes());
                        }
                        else{
                            List<Recipe> currentRecipes = mRecipes.getValue();
                            currentRecipes.addAll(response.body().getRecipes());
                            mRecipes.setValue(currentRecipes);

                            Log.d(TAG, "onResponse: adding more recipes to the list. Now there's " + mRecipes.getValue().size());
                        }

                    }catch (NullPointerException e){
                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                    }

                    mIsPerformingQuery.setValue(false);
                }

                @Override
                public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
                    Log.d(TAG, "onResponse: ERROR: " + t.getMessage());
                    mRecipes.setValue(null);
                    mIsPerformingQuery.setValue(false);
                }
            });
        }

    }

    @Override
    public void onRecipeClick(int position) {
        Log.d(TAG, "onRecipeClick: clicked on a recipe.");
        mSelectedRecipe.setValue(mRecipes.getValue().get(position));
    }

    @Override
    public void onCategoryClick(String category) {
        mIsViewingCategories = false;
        search(category, 0);
    }
}












