package com.codingwithmitch.foodrecipes.requests;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


import retrofit2.Call;
import retrofit2.Response;

import static com.codingwithmitch.foodrecipes.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;
//    private MutableLiveData<Recipe> mRecipe;
//    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private MutableLiveData<Boolean> mRecipeRequestTimeout = new MutableLiveData<>();

    private long lastNetworkRefresh;


    public static RecipeApiClient getInstance(){
        if(instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }

    private RecipeApiClient(){
        mRecipes = new MutableLiveData<>();
//        mRecipe = new MutableLiveData<>();

        lastNetworkRefresh = 0;
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

//    public LiveData<Recipe> getRecipe(){
//        return mRecipe;
//    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeRequestTimeout;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);

//        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
//            @Override
//            public void run() {
//                // let the user know its timed out
//                handler.cancel(true);
//            }
//        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }


    public LiveData<Resource<Recipe>> searchRecipeById(final String recipeId, final RecipeDao dao){
        return new NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.getInstance()){

            @Override
            void saveCallResult(@NonNull RecipeResponse item) {
                lastNetworkRefresh = System.currentTimeMillis();
                Recipe recipe = item.getRecipe();
                dao.insertRecipes(recipe);
            }

            @Override
            boolean shouldFetch(@Nullable Recipe data) {
                return data == null || (System.currentTimeMillis() - lastNetworkRefresh > 300);
            }

            @NonNull
            @Override
            LiveData<Recipe> loadFromDb() {
                return dao.getRecipe(recipeId);
            }

            @NonNull
            @Override
            LiveData<ApiResponse<RecipeResponse>> createCall() {
               return ServiceGenerator.getRecipeApi().getRecipe(
                       Constants.API_KEY,
                       recipeId
               );
            }

            @Override
            protected void onFetchFailed() {
                lastNetworkRefresh = 0;
            }
        }.getAsLiveData();
    }


//    public void searchRecipeById(final String recipeId, final RecipeDao dao){
//        if(mRetrieveRecipeRunnable != null){
//            mRetrieveRecipeRunnable = null;
//        }
//        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId, dao);
//
//        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeRunnable);
//
//        mRecipeRequestTimeout.setValue(false);
//        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
//            @Override
//            public void run() {
//                // let the user know it's timed out
//                mRecipeRequestTimeout.postValue(true);
//                handler.cancel(true);
//            }
//        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
//
//    }

//    private class RetrieveRecipeRunnable implements Runnable{
//
//        private String recipeId;
//        private RecipeDao recipeDao;
//        boolean cancelRequest;
//
//        public RetrieveRecipeRunnable(String recipeId, RecipeDao recipeDao) {
//            this.recipeId = recipeId;
//            this.recipeDao = recipeDao;
//            cancelRequest = false;
//        }
//
//        @Override
//        public void run() {
//            try {
//                Response response = getRecipe(recipeId).execute();
//                if(cancelRequest){
//                    return;
//                }
//                if(response.code() == 200){
//                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
//                    recipeDao.insertRecipes(recipe);
//                }
//                else{
//                    String error = response.errorBody().string();
//                    Log.e(TAG, "run: " + error );
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        private Call<RecipeResponse> getRecipe(String recipeId){
//            return ServiceGenerator.getRecipeApi().getRecipe(
//                    Constants.API_KEY,
//                    recipeId
//            );
//        }
//
//        private void cancelRequest(){
//            Log.d(TAG, "cancelRequest: canceling the search request.");
//            cancelRequest = true;
//        }
//    }

    private class RetrieveRecipesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if(pageNumber == 1){
                        mRecipes.postValue(list);
                    }
                    else{
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(list);
                        mRecipes.postValue(currentRecipes);
                    }
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error );
                    mRecipes.postValue(null);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException: " + e.getMessage());
                mRecipes.postValue(null);
            }
        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber){
            return ServiceGenerator.getRecipeApi().searchRecipe(
                    Constants.API_KEY,
                    query,
                    String.valueOf(pageNumber)
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }



    public void cancelRequest(){
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable.cancelRequest();
        }
//        if(mRetrieveRecipeRunnable != null){
//            mRetrieveRecipeRunnable.cancelRequest();
//        }
    }
}













