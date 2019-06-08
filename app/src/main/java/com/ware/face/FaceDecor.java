package com.ware.face;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceDecor extends RecyclerView.ItemDecoration {
    private final int mRowSpace;
    private final int mColumnSpace;
    private final boolean mIncludeEdge;
    private final Paint mPaint;

    public FaceDecor(int rowSpace, int columnSpace, boolean includeEdge) {
        mRowSpace = rowSpace;
        mColumnSpace = columnSpace;
        mIncludeEdge = includeEdge;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view); // item position

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) layoutManager;
            int orientation = manager.getOrientation();
            int spanCount = manager.getSpanCount();
            int columnOrRow = position % spanCount; // item columnOrRow
            /**
             * VERTICAL时：
             * left/right 类似item的paddingLeft/paddingRight 会影响item的显示宽度
             * top/bottom 类似item的marginLeft/marginRight 不会影响item的显示高
             *
             * HORIZONTAL 规则和上面相反
             */
            if (orientation == GridLayoutManager.VERTICAL) {
                if (mIncludeEdge) {
                    outRect.left = Math.round(mColumnSpace - columnOrRow * 1f * mColumnSpace / spanCount);
                    outRect.right = Math.round((columnOrRow + 1f) * mColumnSpace / spanCount); // (columnOrRow + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = mRowSpace;
                    }
                    outRect.bottom = mRowSpace; // item bottom
                } else {
                    outRect.left = Math.round(columnOrRow * 1f * mColumnSpace / spanCount);
                    outRect.right = Math.round(mColumnSpace - (columnOrRow + 1f) * mColumnSpace / spanCount);
                    if (position >= spanCount) {
                        outRect.top = mRowSpace; // item top
                    }
                }
            } else {
                if (mIncludeEdge) {
                    outRect.top = Math.round(mRowSpace - columnOrRow * 1f * mRowSpace / spanCount);
                    outRect.bottom = Math.round((columnOrRow + 1f) * mRowSpace / spanCount); // (columnOrRow + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.left = mColumnSpace;
                    }
                    outRect.right = mColumnSpace; // item bottom
                } else {
                    outRect.top = Math.round(columnOrRow * 1f * mRowSpace / spanCount);
                    outRect.bottom = Math.round(mRowSpace - (columnOrRow + 1f) * mRowSpace / spanCount);
                    if (position >= spanCount) {
                        outRect.left = mColumnSpace; // item top
                    }
                }
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == RecyclerView.VERTICAL) {
                if (position > 0) {
                    outRect.top = mRowSpace;
                }
            } else {
                if (position > 0) {
                    outRect.left = mColumnSpace;
                }
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            int spanCount = manager.getSpanCount();
            int orientation = manager.getOrientation();

            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int spanIndex = params.getSpanIndex();
            Log.d("FaceDecor", "getItemOffsets: " + spanIndex);

            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                outRect.left = Math.round(spanIndex * 1f * mColumnSpace / spanCount);
                outRect.right = Math.round(mColumnSpace - (spanIndex + 1f) * mColumnSpace / spanCount);
                if (position >= spanCount) {
                    outRect.top = mRowSpace; // item top
                }
            }
        }
    }
}
