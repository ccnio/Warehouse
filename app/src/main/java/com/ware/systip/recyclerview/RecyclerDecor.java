package com.ware.systip.recyclerview;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by jianfeng.li on 19-6-27.
 * <p>
 * update later...
 */
public class RecyclerDecor extends RecyclerView.ItemDecoration {
    private final int mRowSpace;
    private int mColumnSpace;
    private boolean mIncludeEdge;
    private ColorDrawable mDrawable;

    /**
     * LinearLayoutManager
     *
     * @param space
     */
    public RecyclerDecor(int space) {
        this(space, false, 0);
    }

    /**
     * LinearLayoutManager
     *
     * @param space
     * @param includeEdge
     */
    public RecyclerDecor(int space, boolean includeEdge) {
        this(space, includeEdge, 0);
    }

    /**
     * LinearLayoutManager
     *
     * @param space
     * @param includeEdge 是否包含RecyclerView两边
     * @param color
     */
    @SuppressLint("ResourceType")
    public RecyclerDecor(int space, boolean includeEdge, @ColorRes int color) {
        mRowSpace = space;
        mIncludeEdge = includeEdge;
        if (color > 0) {
            mDrawable = new ColorDrawable(color);
        }
    }

    /**
     * GridLayoutManager
     *
     * @param rowSpace
     * @param columnSpace
     */
    public RecyclerDecor(int rowSpace, int columnSpace) {
        this(rowSpace, columnSpace, false);
    }

    /**
     * GridLayoutManager
     *
     * @param rowSpace
     * @param columnSpace
     * @param includeEdge
     */
    public RecyclerDecor(int rowSpace, int columnSpace, boolean includeEdge) {
        mRowSpace = rowSpace;
        mColumnSpace = columnSpace;
        mIncludeEdge = includeEdge;
    }

    @Override
    public void onDraw(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        if (mDrawable == null) return;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                drawLinearVertical(c, parent);
            } else {
                drawLineaHorizontal(c, parent);
            }
        }
    }

    private void drawLinearVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mRowSpace;
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(canvas);
        }
    }

    private void drawLineaHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mColumnSpace;
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(canvas);
        }
    }

    /**
     * grid 每个 item 所占的固定位置是不会变的，只能在些固定位置上填充分离符，必然会占据实际显示位置
     * 所以如果某个 item 间隔大到无法平分空白区域时就不能使用 decoration 了
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view); // item position

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) layoutManager;
            int orientation = manager.getOrientation();
            int spanCount = manager.getSpanCount();
            int columnOrRow = position % spanCount; // item columnOrRow
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
                    outRect.left = mRowSpace;
                }
            }
        }
    }
}
