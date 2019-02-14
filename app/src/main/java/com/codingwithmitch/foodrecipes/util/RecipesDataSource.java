package com.codingwithmitch.foodrecipes.util;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

public class RecipesDataSource extends PageKeyedDataSource<Integer, Recipe> {

    public static final int PAGE_SIZE = 30;


    public RecipesDataSource() {

    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Recipe> callback) {
        List<Recipe> list =
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Recipe> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Recipe> callback) {

    }


}
