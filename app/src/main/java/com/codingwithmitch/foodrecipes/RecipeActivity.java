package com.codingwithmitch.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeViewModel;

import static com.codingwithmitch.foodrecipes.util.Constants.NETWORK_TIMEOUT;

public class RecipeActivity extends BaseActivity{

    private static final String TAG = "RecipeActivity";


    // UI components
    private AppCompatImageView mRecipeImage;
    private TextView mRecipeTitle, mRecipeRank;
    private LinearLayout mRecipeIngredientsContainer;
    private ScrollView mParent;

    private RecipeViewModel mRecipeViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mRecipeImage = findViewById(R.id.recipe_image);
        mRecipeTitle = findViewById(R.id.recipe_title);
        mRecipeRank = findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
        mParent = findViewById(R.id.parent);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        showProgressBar(true);
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("recipe")){
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            subscribeObservers(recipe.getRecipe_id());
        }
    }


    private void subscribeObservers(String recipeId){
        mRecipeViewModel.getRecipe(recipeId).observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if(recipe != null){
                    Log.d(TAG, "onChanged: ---------------------------------------------------------------------------");
                    Log.d(TAG, "onChanged: " + recipe.getTitle());
                    for(String ingredient: recipe.getIngredients()){
                        Log.d(TAG, "onChanged: " + ingredient);
                    }
                    setRecipeProperties(recipe);
                    mRecipeViewModel.setRetrievedRecipe(true);
                }
            }
        });

        mRecipeViewModel.hasNetworkTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
             if(aBoolean && !mRecipeViewModel.didRetrieveRecipe()){
                 Log.e(TAG, "run: Couldn't retrieve data. Likely a network problem.");
                 displayErrorScreen("Error retrieving data. Check network connection.");
             }
            }
        });
    }

    private void displayErrorScreen(String errorMessage){
        mRecipeTitle.setText("Error retrieving recipe...");
        mRecipeRank.setText("");
        TextView textView = new TextView(this);
        if(!errorMessage.equals("")){
            textView.setText(errorMessage);
        }
        else{
            textView.setText("Error");
        }
        textView.setTextSize(15);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        mRecipeIngredientsContainer.addView(textView);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_launcher_background);
        Glide.with(this)
                .setDefaultRequestOptions(options)
                .load(R.drawable.ic_launcher_background)
                .into(mRecipeImage);
        showParent();
        showProgressBar(false);
    }


    private void setRecipeProperties(Recipe recipe){

        if(recipe != null){
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.ic_launcher_background);

            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(recipe.getImage_url())
                    .into(mRecipeImage);

            mRecipeTitle.setText(recipe.getTitle());
            mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

            mRecipeIngredientsContainer.removeAllViews();
            for(String ingredient: recipe.getIngredients()){
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                mRecipeIngredientsContainer.addView(textView);
            }
        }

        showParent();
        showProgressBar(false);
    }

    private void showParent(){
        mParent.setVisibility(View.VISIBLE);
    }
}












