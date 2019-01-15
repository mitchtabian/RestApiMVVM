package com.codingwithmitch.restapimvvm.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration{

    private final int verticalSpaceHeight;

    public VerticalSpacingItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        outRect.top = verticalSpaceHeight;
    }
}
