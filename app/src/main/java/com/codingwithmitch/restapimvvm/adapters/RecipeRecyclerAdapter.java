package com.codingwithmitch.restapimvvm.adapters;

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
import com.codingwithmitch.restapimvvm.R;
import com.codingwithmitch.restapimvvm.models.Recipe;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeRecyclerAdapter";

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;


    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener) {
        mOnRecipeListener = onRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case RECIPE_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new ViewHolder(view, mOnRecipeListener);
            }

            case LOADING_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }

            case CATEGORY_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener);
            }

            default:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new ViewHolder(view, mOnRecipeListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        int itemViewType = getItemViewType(position);
        if(itemViewType == RECIPE_TYPE){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background);

            Glide.with(((ViewHolder)viewHolder).itemView)
                    .setDefaultRequestOptions(options)
                    .load(mRecipes.get(position).getImage_url())
                    .into(((ViewHolder)viewHolder).image);

            ((ViewHolder)viewHolder).title.setText(mRecipes.get(position).getTitle());
            ((ViewHolder)viewHolder).publisher.setText(mRecipes.get(position).getPublisher());
            ((ViewHolder)viewHolder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));


        }
        else if(itemViewType == CATEGORY_TYPE){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background);

            Glide.with(((CategoryViewHolder)viewHolder).itemView)
                    .setDefaultRequestOptions(options)
                    .load(mRecipes.get(position).getImage_url())
                    .into(((CategoryViewHolder)viewHolder).categoryImage);

            ((CategoryViewHolder)viewHolder).categoryTitle.setText(mRecipes.get(position).getTitle());
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(position == mRecipes.size() - 1){
            return LOADING_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else{
            return RECIPE_TYPE;
        }
    }

    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mRecipes != null){
            return mRecipes.size();
        }
        return 0;
    }


    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
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

    /**
     * ViewHolder for Recipe List items
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView title, publisher, socialScore;
        AppCompatImageView image;
        OnRecipeListener listener;


        public ViewHolder(View itemView, OnRecipeListener listener) {
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
            Log.d(TAG, "onClick: called.");
            listener.onRecipeClick(getAdapterPosition());
        }
    }

    public interface OnRecipeListener{
        void onRecipeClick(int position);
        void onCategoryClick(String category);
    }

}
