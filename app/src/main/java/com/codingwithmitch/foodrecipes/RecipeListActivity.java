package com.codingwithmitch.foodrecipes;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;


import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;


public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;

    private TextView mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mTest = findViewById(R.id.test);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        subscribeObservers();

        mRecipeListViewModel.search("barbeque", 1);
    }

    private void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                StringBuilder sb = new StringBuilder();
                for(Recipe recipe: recipes){
                    sb.append(recipe.getTitle() + "\n");
                }
                mTest.setText(sb.toString());
            }
        });
    }



}

















