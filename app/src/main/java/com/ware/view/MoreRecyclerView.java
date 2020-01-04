package com.ware.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jianfeng.li on 19-12-9.
 */
public class MoreRecyclerView extends RecyclerView {


    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

    }

    public MoreRecyclerView(@NonNull Context context) {
        this(context, null, 0);
    }

    public MoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
