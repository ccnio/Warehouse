package com.ware.systip.recyclerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class RecyclerDivider extends RecyclerView.ItemDecoration {
    public static final String TAG = "RecyclerDivider";
    private final int mDividerHeight;
    private Paint paint;
    private final Rect mDividerRect;
    private final Rect mBounds = new Rect();
    private Drawable mDivider;


    public RecyclerDivider(int dividerHeight) {
        paint = new Paint();
        paint.setColor(Color.RED);
        mDividerHeight = dividerHeight;
        mDividerRect = new Rect();
    }

    public RecyclerDivider(int dividerHeight, @ColorInt int color) {
        this(dividerHeight);
        mDividerRect.set(0, 0, 500, dividerHeight);
        mDivider = new ColorDrawable(color);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null || mDivider == null) {
            return;
        }

        int mOrientation = LinearLayoutManager.VERTICAL;
        if (layoutManager instanceof LinearLayoutManager) {
            mOrientation = ((LinearLayoutManager) layoutManager).getOrientation();
        }

        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int top = mBounds.bottom + Math.round(child.getTranslationY());
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        Log.d(TAG, "onDrawOver: ");
        c.drawRect(new Rect(0, 0, 400, 20), paint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0) {
            outRect.top = mDividerHeight;
        }
    }
}
