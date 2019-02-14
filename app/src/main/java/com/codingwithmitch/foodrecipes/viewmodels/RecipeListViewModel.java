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
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;


public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState {CATEGORIES, RECIPES}

    private RecipeRepository mRecipeRepository;
    private MutableLiveData<ViewState> mViewState;
    private MediatorLiveData<Resource<List<Recipe>>> mRecipes = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private String mQuery;
    private int mPageNumber;


    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);

        if(mViewState == null){
            mViewState = new MutableLiveData<>();
            mViewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return mRecipes;
    }

    public LiveData<ViewState> getViewState(){
        return mViewState;
    }

    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public void setViewCategories(){
        mViewState.setValue(ViewState.CATEGORIES);
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(pageNumber == 0){
            pageNumber = 1;
        }
        mPageNumber = pageNumber;
        mQuery = query;
        mIsQueryExhausted.setValue(false);
        executeSearch();
    }

    public void searchNextPage(){
        if(!mIsQueryExhausted.getValue()){
            mPageNumber++;
            executeSearch();
        }
    }

    private void executeSearch(){
        mViewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = mRecipeRepository.searchRecipesApi(mQuery, mPageNumber);
        mRecipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    mRecipes.setValue(listResource);

                    if( listResource.status == Resource.Status.SUCCESS ){
                        if(listResource.data != null) {
                            if (listResource.data.size() % 30 != 0 || listResource.data.size() == 0) {
                                Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                mIsQueryExhausted.setValue(true);
                            }
                        }
                        // must remove or it will keep listening to repository
                        mRecipes.removeSource(repositorySource);
                    }
                }
            }
        });
    }

}















