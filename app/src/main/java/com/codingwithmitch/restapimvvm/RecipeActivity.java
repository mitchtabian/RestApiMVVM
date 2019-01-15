package com.codingwithmitch.restapimvvm;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.restapimvvm.models.Recipe;
import com.codingwithmitch.restapimvvm.viewmodels.RecipeViewModel;

public class RecipeActivity extends BaseActivity {

    private static final String TAG = "RecipeActivity";

    // ui components
    private AppCompatImageView mRecipeImage;
    private TextView mRecipeTitle, mRecipeRank;
    private LinearLayout mRecipeIngredientsContainer;
    private ConstraintLayout mConstraintLayout;
    private RecipeViewModel mRecipeViewModel;

    // vars
    private Recipe mRecipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mRecipeImage = findViewById(R.id.recipe_image);
        mRecipeTitle = findViewById(R.id.recipe_title);
        mRecipeRank = findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
        mConstraintLayout = findViewById(R.id.parent);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        getIncomingIntent();
        initObservers();

        showProgressBar(true);
    }


    private void initObservers(){
        mRecipeViewModel.getQueryError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.e(TAG, "onChanged: Error retrieving recipe." );
                showProgressBar(false);
                setErrorScreen();
            }
        });

        mRecipeViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                mRecipe = recipe;
                setRecipeProperties();
            }
        });
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("recipe")){
            mRecipe = getIntent().getParcelableExtra("recipe");
            mRecipeViewModel.search(mRecipe.getRecipe_id());
        }
    }

    private void setErrorScreen(){
        mRecipeTitle.setText("Error retrieving recipe...");
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_launcher_background);
        Glide.with(this)
                .setDefaultRequestOptions(options)
                .load(R.drawable.ic_launcher_background)
                .into(mRecipeImage);
        showParent();
    }

    private void setRecipeProperties(){

        if(mRecipe != null){
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.ic_launcher_background);

            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(mRecipe.getImage_url())
                    .into(mRecipeImage);

            mRecipeTitle.setText(mRecipe.getTitle());
            mRecipeRank.setText(String.valueOf(Math.round(mRecipe.getSocial_rank())));

            for(String ingredient: mRecipe.getIngredients()){
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                mRecipeIngredientsContainer.addView(textView);
            }
        }

        showProgressBar(false);
        showParent();
    }

    private void showParent(){
        mConstraintLayout.setVisibility(View.VISIBLE);
    }

}






















