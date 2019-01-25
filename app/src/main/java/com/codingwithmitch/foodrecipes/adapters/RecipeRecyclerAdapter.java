package com.codingwithmitch.foodrecipes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;

    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener) {
        mOnRecipeListener = onRecipeListener;
        mRecipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
        return new RecipeViewHolder(view, mOnRecipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        // set the image
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_launcher_background);

        Glide.with(((RecipeViewHolder) viewHolder).itemView)
                .setDefaultRequestOptions(options)
                .load(mRecipes.get(i).getImage_url())
                .into(((RecipeViewHolder) viewHolder).image);

        ((RecipeViewHolder) viewHolder).title.setText(mRecipes.get(i).getTitle());
        ((RecipeViewHolder) viewHolder).publisher.setText(mRecipes.get(i).getPublisher());
        ((RecipeViewHolder) viewHolder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(i).getSocial_rank())));
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
    }
}

























