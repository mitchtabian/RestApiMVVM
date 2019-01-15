package com.codingwithmitch.restapimvvm;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codingwithmitch.restapimvvm.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.restapimvvm.models.Recipe;
import com.codingwithmitch.restapimvvm.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.restapimvvm.viewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";

    // UI components
    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private View mRootView;

    // vars

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRootView = findViewById(R.id.root_view);
        mRecyclerView = findViewById(R.id.recipe_list);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        initRecyclerView();

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRootView.requestFocus();
        hideKeyboard();
    }


    private void initRecyclerView(){
        final RecipeRecyclerAdapter adapter = new RecipeRecyclerAdapter(mRecipeListViewModel);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d(TAG, "onChanged: updating list with new recipes.");
                adapter.setRecipes(recipes);

                if(recipes != null){

                    if(recipes.size() > 0){
                        showList();
                    }
                    else{
                        hideList();
                    }
                }
                else{
                    hideList();
                }

//                showProgressBar(false);

//                mRecyclerView.smoothScrollToPosition(recipes.size() - 1);
            }
        });

//        mRecipeListViewModel.getIsPerformingQuery().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                showProgressBar(aBoolean);
//            }
//        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!mRecyclerView.canScrollVertically(1)){
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });

        mRecipeListViewModel.getSelectedRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                Log.d(TAG, "onChanged: a recipe has been selected: " + recipe.toString());
                Intent intent = new Intent(RecipeListActivity.this, RecipeActivity.class);
                intent.putExtra("recipe", recipe);
                startActivity(intent);
            }
        });

        mRecipeListViewModel.setSearchCategories();
    }

    @Override
    public void onBackPressed() {
        if(!mRecipeListViewModel.getIsViewingCategories()){
            mRecipeListViewModel.setSearchCategories();
        }else{
            super.onBackPressed();
        }

    }

    private void showList(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideList(){
        mRecyclerView.setVisibility(View.GONE);

    }

    private void initSearchView(){
        mSearchView = findViewById(R.id.search_view);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "onQueryTextSubmit: " + query);

//                showProgressBar(true);

                // Search the database for a recipe
                mRecipeListViewModel.search(query, 0);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_categories){
            mRecipeListViewModel.setSearchCategories();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        initSearchView();
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
















