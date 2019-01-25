package com.codingwithmitch.foodrecipes.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codingwithmitch.foodrecipes.R;

/**
 * ViewHolder for Recipe List items
 */
public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    TextView title, publisher, socialScore;
    AppCompatImageView image;
    OnRecipeListener onRecipeListener;

    public RecipeViewHolder(View itemView, OnRecipeListener onRecipeListener){
        super(itemView);

        this.onRecipeListener = onRecipeListener;

        title = itemView.findViewById(R.id.recipe_title);
        publisher = itemView.findViewById(R.id.recipe_publisher);
        socialScore = itemView.findViewById(R.id.recipe_social_score);
        image = itemView.findViewById(R.id.recipe_image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onRecipeListener.onRecipeClick(getAdapterPosition());
    }
}