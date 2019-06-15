package com.ware.systip.recyclerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexLine;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;


/**
 * Created by jianfeng.li on 19-6-5.
 * <p>
 * todo grid/staggered drawable divider need update
 */
public class RecyclerDecor extends RecyclerView.ItemDecoration {
    private final int mRowSpace;
    private final int mColumnSpace;
    private final boolean mIncludeEdge;
    private final Paint mPaint;
    private final ColorDrawable mDrawable;

    public RecyclerDecor(int rowSpace, int columnSpace, boolean includeEdge) {
        mRowSpace = rowSpace;
        mColumnSpace = columnSpace;
        mIncludeEdge = includeEdge;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mDrawable = new ColorDrawable(Color.RED);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            Log.d("RecyclerDecor", "onDraw: ");
            int orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == GridLayoutManager.VERTICAL) {
                drawGridVertical(c, parent);
                drawGridHorizontal(c, parent);
            }
            return;
        }
        if (layoutManager instanceof LinearLayoutManager) {
            Log.d("RecyclerDecor", "onDraw: 111");
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                drawLinearVertical(c, parent);
                drawLineaHorizontal(c, parent);
            } else {
            }
            return;
        }
    }


    private void drawGridHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mRowSpace;

            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    private void drawGridVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin + mRowSpace;
            final int left = child.getRight() + params.rightMargin;
            int right = left + mColumnSpace;
//            //满足条件( 最后一行 && 不绘制 ) 将vertical多出的一部分去掉;
            if (i == childCount - 1) {
                right -= mColumnSpace;
            }
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    public void drawLinearVertical(Canvas canvas, RecyclerView parent) {
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

    public void drawLineaHorizontal(Canvas canvas, RecyclerView parent) {
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
        } else if (layoutManager instanceof FlexboxLayoutManager) { /**see {@link com.google.android.flexbox.FlexboxItemDecoration}**/
            FlexboxLayoutManager manager = (FlexboxLayoutManager) layoutManager;
            int direction = manager.getFlexDirection();
            if (direction == FlexDirection.ROW || direction == FlexDirection.ROW_REVERSE) {
                List<FlexLine> flexLines = manager.getFlexLines();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            int spanCount = manager.getSpanCount();
            int orientation = manager.getOrientation();

            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int spanIndex = params.getSpanIndex(); //由于同一行的views pos布局没有规则，所以不能通过  int columnOrRow = position % spanCount 获取
//            spanSize = params.isFullSpan() ? spanCount : 1;
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
