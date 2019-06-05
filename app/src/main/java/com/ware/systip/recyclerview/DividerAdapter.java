package com.ware.systip.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ware.R;

import java.util.ArrayList;

public class DividerAdapter extends RecyclerView.Adapter<DividerAdapter.MHolder> implements View.OnClickListener {
    private final ArrayList<String> mData;
    private final LayoutInflater layoutInflater;

    public DividerAdapter(ArrayList<String> strings, RecyclerDividerActivity recyclerDividerActivity) {
        this.mData = strings;
        layoutInflater = LayoutInflater.from(recyclerDividerActivity);
    }

    @NonNull
    @Override
    public MHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_capture, parent, false);
        view.setOnClickListener(this);
        view.setBackgroundResource(R.drawable.item_selector);
        return new MHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MHolder holder, int position) {
        holder.mTextView.setText(mData.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        Toast.makeText(v.getContext(), "this is " + pos, Toast.LENGTH_SHORT).show();
    }

    class MHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public MHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_name);
        }
    }
}
