package com.codingwithmitch.foodrecipes.requests;


import android.arch.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    // SEARCH
    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipe(
            @Query("key") String key,
            @Query("q") String query,
            @Query("page") String page
    );

//    // GET RECIPE REQUEST
//    @GET("api/get")
//    Call<RecipeResponse> getRecipe(
//            @Query("key") String key,
//            @Query("rId") String recipe_id
//    );

    // GET RECIPE REQUEST
    @GET("api/get")
    Call<ApiResponse<RecipeResponse>> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id
    );

//    // GET RECIPE REQUEST
//    @GET("api/get")
//    LiveData<ApiResponse<RecipeResponse>> getRecipe(
//            @Query("key") String key,
//            @Query("rId") String recipe_id
//    );

}

















