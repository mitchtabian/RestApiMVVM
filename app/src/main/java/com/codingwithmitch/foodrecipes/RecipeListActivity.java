package com.codingwithmitch.foodrecipes;


import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.TextView;


import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;


public class RecipeListActivity extends BaseActivity implements RecipeRecyclerAdapter.OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;

    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        subscribeObservers();
        initSearchView();
        mRecipeListViewModel.displaySearchCategories();
    }

    private void initRecyclerView(){
        mAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d(TAG, "onChanged: updating list with new recipes. Num recipes: " + recipes.size());
                mAdapter.setRecipes(recipes);
            }
        });
    }

    private void initSearchView(){
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);

                // Search the database for a recipe
                mRecipeListViewModel.setIsViewingRecipes(true);
                mRecipeListViewModel.search(query, 0);
                mRecyclerView.requestFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                // Wait for the user to submit the search. So do nothing here.

                return false;
            }
        });
    }


    @Override
    public void onRecipeClick(int position) {
        Log.d(TAG, "onRecipeClick: clicked a recipe at position: " + position);
    }

    @Override
    public void onCategoryClick(String category) {
        mRecipeListViewModel.setIsViewingRecipes(true);
        mRecipeListViewModel.search(category, 0);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called.");
        if(mRecipeListViewModel.getIsPerformingQuery()){
            mRecipeListViewModel.cancelQuery();
            mRecipeListViewModel.displaySearchCategories();
        }
        else{
            if(mRecipeListViewModel.getIsViewingRecipes()){
                mRecipeListViewModel.displaySearchCategories();
            }else{
                super.onBackPressed();
            }
        }
    }
}

















