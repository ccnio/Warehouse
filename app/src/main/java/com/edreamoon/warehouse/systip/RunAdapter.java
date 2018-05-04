package com.edreamoon.warehouse.systip;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.edreamoon.warehouse.R;

import java.util.List;

public class RunAdapter extends BaseAdapter {

    private static final String TAG = "RunAdapter";
    private final LayoutInflater mInflater;
    private List<String> mData;

    private boolean more;
    public static final int SHOW_NUM = 13;

    public RunAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RunInfoHolder infoHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_run_info, null);
            infoHolder = new RunInfoHolder(convertView);
            convertView.setTag(infoHolder);
        } else {
            infoHolder = (RunInfoHolder) convertView.getTag();
        }

        convertView.setBackgroundColor((position & 1) == 0 ? 0xffF7F7F7 : Color.WHITE);
        return convertView;
    }

    @Override
    public int getCount() {
        if (mData == null) return 0;
        return more ? mData.size() : Math.min(SHOW_NUM, mData.size());
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void setMore(boolean more) {
        this.more = more;
        notifyDataSetChanged();
    }
}
