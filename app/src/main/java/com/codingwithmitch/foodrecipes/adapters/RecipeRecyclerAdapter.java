package com.codingwithmitch.foodrecipes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;

    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener) {
        mOnRecipeListener = onRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = null;

        switch (i) { // i is the view type constant
            case RECIPE_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewholder(view, mOnRecipeListener);
            }

            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }

            case CATEGORY_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_category_list_item, viewGroup, false);
                return new CategoryViewHolder(view, mOnRecipeListener);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }

            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewholder(view, mOnRecipeListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int itemViewType = getItemViewType(i);
        if(itemViewType == RECIPE_TYPE) {
            // set the image
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background);

            Glide.with(((RecipeViewholder) viewHolder).itemView)
                    .setDefaultRequestOptions(options)
                    .load(mRecipes.get(i).getImage_url())
                    .into(((RecipeViewholder) viewHolder).image);


            ((RecipeViewholder) viewHolder).title.setText(mRecipes.get(i).getTitle());
            ((RecipeViewholder) viewHolder).publisher.setText(mRecipes.get(i).getPublisher());
            ((RecipeViewholder) viewHolder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(i).getSocial_rank())));
        }
        else if(itemViewType == CATEGORY_TYPE){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background);

            Glide.with(((CategoryViewHolder)viewHolder).itemView)
                    .setDefaultRequestOptions(options)
                    .load(mRecipes.get(i).getImage_url())
                    .into(((CategoryViewHolder)viewHolder).categoryImage);

            ((CategoryViewHolder)viewHolder).categoryTitle.setText(mRecipes.get(i).getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(position == mRecipes.size() - 1
                && !mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return LOADING_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else{
            return RECIPE_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if(mRecipes != null){
            return mRecipes.size();
        }
        return 0;
    }

    public void setQueryExhausted(){
        Recipe exhaustedMarkerRecipe = new Recipe();
        exhaustedMarkerRecipe.setTitle("EXHAUSTED...");
        mRecipes.add(exhaustedMarkerRecipe);
    }

    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public class SearchExhaustedViewHolder extends RecyclerView.ViewHolder {

        public SearchExhaustedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * ViewHolder for Recipe List items
     */
    public class RecipeViewholder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView title, publisher, socialScore;
        AppCompatImageView image;
        OnRecipeListener listener;


        public RecipeViewholder(View itemView, OnRecipeListener listener) {
            super(itemView);
            this.listener = listener;
            title = itemView.findViewById(R.id.recipe_title);
            publisher = itemView.findViewById(R.id.recipe_publisher);
            socialScore = itemView.findViewById(R.id.recipe_social_score);
            image = itemView.findViewById(R.id.recipe_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecipeClick(getAdapterPosition());
        }
    }

    public interface OnRecipeListener{
        void onRecipeClick(int position);
        void onCategoryClick(String category);
    }

}
















