package com.codingwithmitch.foodrecipes.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codingwithmitch.foodrecipes.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ViewHolder for search categories
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener
{
    CircleImageView categoryImage;
    TextView categoryTitle;
    OnRecipeListener listener;

    public CategoryViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
        super(itemView);
        categoryImage = itemView.findViewById(R.id.category_image);
        categoryTitle = itemView.findViewById(R.id.category_title);
        listener = onRecipeListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.onCategoryClick(categoryTitle.getText().toString());
    }
}
