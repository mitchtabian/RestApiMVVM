package com.codingwithmitch.foodrecipes.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.codingwithmitch.foodrecipes.MyApplication;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.RecipeApi;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.io.IOException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecipeRepository {

    private static final String TAG = "RecipeRepository";

    private static RecipeRepository instance;
    private RecipeApi mRecipeApi;
    private String mQuery;
    private int mPageNumber;

    private MutableLiveData<List<Recipe>> mObservableRecipes = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();


    private MutableLiveData<Recipe> mRecipe = new MutableLiveData<>();
    private MutableLiveData<String> mRecipeQueryError = new MutableLiveData<>();

    // Calls
    private Call<RecipeSearchResponse> mRecipeSearchCall = null;

    public static RecipeRepository getInstance(Application application){
        if(instance == null){
            instance = new RecipeRepository(((MyApplication)application).getRetrofit().create(RecipeApi.class));
        }
        return instance;
    }


    private RecipeRepository(RecipeApi recipeApi) {
        mRecipeApi = recipeApi;
        mQuery = "";
        mPageNumber = 0;
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mObservableRecipes;
    }

    public LiveData<Boolean> isQueryExhausted() {
        return mIsQueryExhausted;
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public LiveData<String> getRecipeQueryError(){
        return mRecipeQueryError;
    }

    public void searchApi(String query, int pageNumber){
        mQuery = query;
        mPageNumber = pageNumber;
        mIsQueryExhausted.setValue(false);

        mRecipeSearchCall = mRecipeApi
                .searchRecipe(
                        Constants.API_KEY,
                        mQuery,
                        String.valueOf(mPageNumber)
                );

        mRecipeSearchCall.enqueue(recipeListSearchCallback);
    }

    public void searchNextPage(){
        searchApi(mQuery, mPageNumber + 1);
    }


    public void searchForRecipe(String recipeId){
        Call<RecipeResponse> responseCall = mRecipeApi
                .getRecipe(
                        Constants.API_KEY,
                        recipeId
                );

        responseCall.enqueue(singleRecipeCallback);
    }

    private Callback<RecipeSearchResponse> recipeListSearchCallback = new Callback<RecipeSearchResponse>() {
        @Override
        public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
            if(response.code() == 200){
                Log.d(TAG, "onResponse: " + response.body().toString());
            }
            else {
                try {
                    Log.d(TAG, "onResponse: " + response.errorBody().string());
                    mIsQueryExhausted.setValue(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Set results to mRecipes list
            try{
                if(mPageNumber == 0){
                    mObservableRecipes.setValue(response.body().getRecipes());
                }
                else{
                    List<Recipe> currentRecipes = mObservableRecipes.getValue();
                    currentRecipes.addAll(response.body().getRecipes());
                    mObservableRecipes.setValue(currentRecipes);
                }
                if(response.body().getRecipes().size() < 30){
                    mIsQueryExhausted.setValue(true);
                }

            }catch (NullPointerException e){
                Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
            }
        }

        @Override
        public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
            Log.d(TAG, "onResponse: ERROR: " + t.getMessage());
            mIsQueryExhausted.setValue(true);
        }
    };

    /**
     * Callback for retrieving a single recipe given a recipe id.
     */
    private Callback<RecipeResponse>  singleRecipeCallback = new Callback<RecipeResponse>() {
        @Override
        public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
            if(response.code() == 200){
                Log.d(TAG, "onResponse: " + response.body().toString());
            }
            else {
                try {
                    Log.d(TAG, "onResponse: " + response.errorBody().string());
                    mRecipeQueryError.setValue("Couldn't retrieve the recipe. Check API key.");
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mRecipe.setValue(response.body().getRecipe());
        }

        @Override
        public void onFailure(Call<RecipeResponse> call, Throwable t) {
            mRecipeQueryError.setValue(t.getMessage());
        }
    };


    public void cancelQuery() {
        if(mRecipeSearchCall != null){
            mRecipeSearchCall.cancel();
            mRecipeSearchCall = null;
        }
    }
}
















