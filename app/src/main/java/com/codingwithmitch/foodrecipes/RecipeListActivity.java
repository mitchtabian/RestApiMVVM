package com.codingwithmitch.foodrecipes;

import android.os.Bundle;
import android.view.View;

import retrofit2.Retrofit;

public class RecipeListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProgressBar.getVisibility() == View.VISIBLE){
                    showProgressBar(false);
                }
                else{
                    showProgressBar(true);
                }
            }
        });

        Retrofit retrofit = ((MyApplication) getApplication()).getRetrofit();
    }
}
