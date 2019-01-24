package com.codingwithmitch.foodrecipes.requests;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.codingwithmitch.foodrecipes.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;
    private RefreshRecipeRunnable mRefreshRecipeRunnable;
    private MutableLiveData<List<Recipe>> mRecipes = new MutableLiveData<>();
    private MutableLiveData<Boolean> mHasNetworkTimedOut = new MutableLiveData<>();

    public static RecipeApiClient getInstance(){
        if(instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }


    private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber){
        return ServiceGenerator.getRecipeApi().searchRecipe(
                Constants.API_KEY,
                query,
                String.valueOf(pageNumber));
    }

    private Call<RecipeResponse> getRecipe(String recipeId){
        return ServiceGenerator.getRecipeApi()
                .getRecipe(
                        Constants.API_KEY,
                        recipeId
                );
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    public LiveData<Boolean> hasNetworkTimedOut(){
        return mHasNetworkTimedOut;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        AppExecutors.get().networkIO().execute(mRetrieveRecipesRunnable);
    }

    public void refreshRecipe(final String recipeId, final RecipeDao dao){
        mHasNetworkTimedOut.setValue(false);
        if(mRefreshRecipeRunnable != null){
            mRefreshRecipeRunnable = null;
        }
        mRefreshRecipeRunnable = new RefreshRecipeRunnable(recipeId, dao);
//        AppExecutors.get().networkIO().execute(mRefreshRecipeRunnable);

        final Future handler = AppExecutors.get().networkIO().submit(mRefreshRecipeRunnable);

        // Set a timeout for the data refresh
        AppExecutors.get().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                mHasNetworkTimedOut.postValue(true);
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RefreshRecipeRunnable implements Runnable{

        private String recipeId;
        private RecipeDao recipeDao;
        private boolean cancelRequest;

        private RefreshRecipeRunnable(String recipeId, RecipeDao dao) {
            this.recipeId = recipeId;
            cancelRequest = false;
            this.recipeDao = dao;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if(cancelRequest){
                    Log.d(TAG, "run: canceling the request.");
                    return;
                }
                Log.d(TAG, "run: response code: " + response.code());
                if(response.code() == 200){
                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
                    recipeDao.insertRecipes(recipe);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: error: " + error );
                }
            } catch (IOException e) {
                Log.e(TAG, "run: IOException: " + e.getMessage());
            }
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the refresh query.");
            cancelRequest = true;
        }
    }

    private class RetrieveRecipesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        private boolean cancelRequest;

        private RetrieveRecipesRunnable(String query, int pageNumber) {
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
                else{ // something is wrong, query the cache
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: error: " + error);
                    mRecipes.postValue(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the retrieval query");
            cancelRequest = true;
        }
    }


    public void cancelRequest() {
        if(mRefreshRecipeRunnable != null){
            mRefreshRecipeRunnable.cancelRequest();
        }
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable.cancelRequest();
        }
    }

}
















