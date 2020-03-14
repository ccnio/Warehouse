package com.ware.widget.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ware.R;

/**
 */

public class MGridAdapter extends BaseAdapter implements View.OnClickListener {
    private final Context mContext;

    public MGridAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null || view.getTag() == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_capture, viewGroup, false);
            holder.textView = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.textView.setText(i + "  ");
        return view;
    }

    @Override
    public void onClick(View v) {
//
    }

    public class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

}
