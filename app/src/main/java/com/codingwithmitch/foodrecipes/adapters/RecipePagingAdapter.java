package com.codingwithmitch.foodrecipes.adapters;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;

public class RecipePagingAdapter extends PagedListAdapter<Recipe, RecyclerView.ViewHolder> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private OnRecipeListener mOnRecipeListener;

    public RecipePagingAdapter(OnRecipeListener onRecipeListener) {
        super(DIFF_CALLBACK);
        mOnRecipeListener = onRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;

        switch (i){

            case RECIPE_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, mOnRecipeListener, getGlideRequestManager(viewGroup));
            }
            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, mOnRecipeListener, getGlideRequestManager(viewGroup));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        Recipe recipe = getItem(position);
        if (recipe != null) {
            ((RecipeViewHolder)holder).bind(recipe);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
//            holder.clear();
        }
    }

    private RequestManager getGlideRequestManager(ViewGroup viewGroup){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.white_background);
        return Glide.with(viewGroup.getContext()).setDefaultRequestOptions(requestOptions);
    }

    private static DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Recipe>() {
                // Concert details may have changed if reloaded from the database,
                // but ID is fixed.
                @Override
                public boolean areItemsTheSame(Recipe oldRecipe, Recipe newRecipe) {
                    return oldRecipe.getRecipe_id() == newRecipe.getRecipe_id();
                }

                @Override
                public boolean areContentsTheSame(Recipe oldRecipe,
                                                  Recipe newRecipe) {
                    return oldRecipe.equals(newRecipe);
                }
            };

    @Override
    public void submitList(PagedList<Recipe> pagedList) {
        super.submitList(pagedList);
    }
}
