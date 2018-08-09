package com.edreamoon.warehouse.systip;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edreamoon.Utils;
import com.edreamoon.warehouse.R;
import com.edreamoon.warehouse.kt.KtAdapter;

import java.util.ArrayList;

public class StaggeredAdapter extends RecyclerView.Adapter<StaggeredAdapter.CommonHolder> {


    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 3;
    private final ArrayList<String> mData;
    private final ArrayList<Integer> mSizes;

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
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_header, parent, false));
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
            recyclerView.setAdapter(new KtAdapter(mTextView.getContext()));
        }
    }
}
