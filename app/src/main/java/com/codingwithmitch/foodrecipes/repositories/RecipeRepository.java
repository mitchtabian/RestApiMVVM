package com.codingwithmitch.foodrecipes.repositories;

import android.app.Application;
import android.util.Log;

import com.codingwithmitch.foodrecipes.MyApplication;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.RecipeApi;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecipeRepository implements RequestCancelListener{

    private static final String TAG = "RecipeRepository";

    private static RecipeRepository instance;
    private RecipeApi mRecipeApi;
    private RecipeListCallback mRecipeListCallback;

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
    }

    public void setRecipeListCallback(RecipeListCallback callback){
        mRecipeListCallback = callback;
    }

    public void searchApi(String query, int pageNumber){
        mRecipeListCallback.onQueryStart();
        mRecipeSearchCall = mRecipeApi
                .searchRecipe(
                        Constants.API_KEY,
                        query,
                        String.valueOf(pageNumber)
                );

        mRecipeSearchCall.enqueue(recipeListSearchCallback);
    }

    private Callback<RecipeSearchResponse> recipeListSearchCallback = new Callback<RecipeSearchResponse>() {
        @Override
        public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
            if(response.code() == 200){
                Log.d(TAG, "onResponse: " + response.body().toString());
                List<Recipe> recipes = new ArrayList<>(response.body().getRecipes());
                mRecipeListCallback.setRecipes(recipes);
                for(Recipe recipe: recipes){
                    Log.d(TAG, "onResponse: " + recipe.toString());
                }
            }
            else {
                try {
                    Log.d(TAG, "onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mRecipeListCallback.onQueryDone();
        }

        @Override
        public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
            Log.d(TAG, "onResponse: ERROR: " + t.getMessage());
            mRecipeListCallback.onQueryDone();
        }
    };

    @Override
    public void onCancel() {
        if(mRecipeSearchCall != null){
            mRecipeSearchCall.cancel();
            mRecipeListCallback.onQueryDone();
            mRecipeSearchCall = null;
        }
    }
}
















