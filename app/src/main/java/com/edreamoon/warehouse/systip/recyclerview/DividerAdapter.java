package com.edreamoon.warehouse.systip.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edreamoon.Utils;
import com.edreamoon.warehouse.R;

import java.util.ArrayList;

public class DividerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<String> mData;
    private final LayoutInflater layoutInflater;

    public DividerAdapter(ArrayList<String> strings) {
        this.mData = strings;
        layoutInflater = LayoutInflater.from(Utils.mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MHolder(layoutInflater.inflate(R.layout.item_capture, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MHolder extends RecyclerView.ViewHolder {

        public MHolder(View itemView) {
            super(itemView);
        }
    }
}
