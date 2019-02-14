package com.codingwithmitch.foodrecipes.viewmodels;


import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
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
    private LiveData<Resource<List<Recipe>>> mRepositoryResult;
    private LiveData<PagedList<Recipe>> mRecipes;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private int mPageNumber;


    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);

        if(mViewState == null){
            mViewState = new MutableLiveData<>();
            mViewState.setValue(ViewState.CATEGORIES);
        }

        mRepositoryResult = Transformations.map(mQuery, new Function<String, Resource<List<Recipe>>>() {
            @Override
            public Resource<List<Recipe>> apply(String input) {
                return mRecipeRepository.searchRecipesApi(input, 1).getValue();
            }
        });

        mRecipes = Transformations.switchMap(mRepositoryResult, new Function<Resource<List<Recipe>>, LiveData<PagedList<Recipe>>>() {
            @Override
            public LiveData<PagedList<Recipe>> apply(Resource<List<Recipe>> input) {

                final MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
                recipes.setValue(input.data);

                PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setInitialLoadSizeHint(30)
                        .setPageSize(30).build();

                return
            }
        });
    }

    public LiveData<PagedList<Recipe>> getRecipes(){
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

    public void searchRecipesApi(String query){
        mQuery.setValue(query);
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

        mRecipes = Transformations.map(repositorySource, new Function<Resource<List<Recipe>>, PagedList<Recipe>>() {
            @Override
            public PagedList<Recipe> apply(Resource<List<Recipe>> input) {
                if(input != null){
                    return (PagedList<Recipe>) input.data;
                }
                return null;
            }
        });
    }

//    private void executeSearch(){
//        mViewState.setValue(ViewState.RECIPES);
//        final LiveData<Resource<List<Recipe>>> repositorySource = mRecipeRepository.searchRecipesApi(mQuery, mPageNumber);
//        mRecipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
//            @Override
//            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
//                if(listResource != null){
//                    mRecipes.setValue(listResource);
//
//                    if( listResource.status == Resource.Status.SUCCESS ){
//                        if(listResource.data != null) {
//                            if (listResource.data.size() % 30 != 0 || listResource.data.size() == 0) {
//                                Log.d(TAG, "onChanged: query is EXHAUSTED...");
//                                mIsQueryExhausted.setValue(true);
//                            }
//                        }
//                        // must remove or it will keep listening to repository
//                        mRecipes.removeSource(repositorySource);
//
//                    }
//                }
//            }
//        });
//    }

}















