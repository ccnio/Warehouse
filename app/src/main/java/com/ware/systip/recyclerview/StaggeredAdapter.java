package com.ware.systip.recyclerview;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edreamoon.Utils;
import com.ware.R;
import com.ware.kt.KtAdapter;

import java.util.ArrayList;

public class StaggeredAdapter extends RecyclerView.Adapter<StaggeredAdapter.CommonHolder> {


    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 3;
    private final ArrayList<String> mData;
    private final ArrayList<Integer> mSizes;

    private int mPreTop;
    private boolean aBoolean;
    private int mPreState;
    public RecyclerView.OnScrollListener mListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mPreState == RecyclerView.SCROLL_STATE_SETTLING || mPreState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Log.d("statre", "  " + ((recyclerView.computeVerticalScrollOffset() - mPreTop) > 0));
                }
                Log.d("onScrollStateChanged", " 1111 " + aBoolean);
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if(mPreState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Log.d("statre", "  " + ((recyclerView.computeVerticalScrollOffset() - mPreTop) > 0));
                }
                Log.d("onScrollStateChanged", "2222222" + aBoolean);
                mPreTop = recyclerView.computeVerticalScrollOffset();
            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                Log.d("onScrollStateChanged", "33" + aBoolean);
            }

            mPreState = newState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            aBoolean = dy > 0;
//            Log.d("22onScrollStateChanged", "onScrolled: " + dy);
        }
    };
    private HeaderHolder headerHolder;

    public StaggeredAdapter(ArrayList<String> strings) {
        mData = strings;
        mSizes = new ArrayList<>();
        mSizes.add((int) Utils.dp2px(102));
        mSizes.add((int) Utils.dp2px(153));
        mSizes.add((int) Utils.dp2px(90));
    }


    @Override
    public void onViewAttachedToWindow(CommonHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) lp;
            if (holder.getItemViewType() == TYPE_HEADER || holder.getItemViewType() == TYPE_FOOTER) {
                params.setFullSpan(true);
            } else {
                params.setFullSpan(false);
            }
        }
    }

    @NonNull
    @Override
    public CommonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            headerHolder = new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_header, parent, false));
            return headerHolder;
        }
        if (viewType == TYPE_FOOTER) {
            return new CommonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capture, parent, false));
        }

        return new CommonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommonHolder holder, int position) {

        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            return;
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            int index = position % 3;
            holder.mTextView.setHeight(mSizes.get(index));
            holder.mTextView.setBackgroundColor(index == 0 ? Color.LTGRAY : index == 1 ? Color.DKGRAY : Color.GRAY);
            holder.mTextView.setText(mData.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 2 : mData.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
//        if (mHeaderView == null && mFooterView == null) {
//            return TYPE_NORMAL;
//        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    class CommonHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public CommonHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_name);
        }
    }

    class HeaderHolder extends CommonHolder {

        public TextView mTextView;

        public HeaderHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_name);
            RecyclerView recyclerView = itemView.findViewById(R.id.mRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(mTextView.getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new KtAdapter(mTextView.getContext(), "Hori"));
        }
    }
}
