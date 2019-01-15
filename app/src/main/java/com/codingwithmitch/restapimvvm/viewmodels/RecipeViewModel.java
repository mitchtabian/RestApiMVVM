package com.codingwithmitch.restapimvvm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.codingwithmitch.restapimvvm.MyApplication;
import com.codingwithmitch.restapimvvm.models.Recipe;
import com.codingwithmitch.restapimvvm.requests.RecipeApi;
import com.codingwithmitch.restapimvvm.requests.responses.RecipeResponse;
import com.codingwithmitch.restapimvvm.util.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecipeViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeViewModel";

    private MutableLiveData<Recipe> mRecipe;
    private MutableLiveData<Boolean> mQueryError;
    private RecipeApi mRecipeApi;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipe = new MutableLiveData<>();
        mQueryError = new MutableLiveData<>();
        mRecipeApi = ((MyApplication) application).getRetrofit().create(RecipeApi.class);
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public LiveData<Boolean> getQueryError(){
        return mQueryError;
    }

    public void search(String recipeId) {

        Call<RecipeResponse> responseCall = mRecipeApi
                .getRecipe(
                        Constants.API_KEY,
                        recipeId
                );

        responseCall.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if(response.code() == 200){
                    Log.d(TAG, "onResponse: " + response.body().toString());
                }
                else {
                    try {
                        Log.d(TAG, "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mRecipe.setValue(response.body().getRecipe());
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                mQueryError.setValue(true);
            }
        });
    }
}















