package com.edreamoon.warehouse.systip;

import android.view.View;
import android.widget.TextView;

import com.edreamoon.warehouse.R;


public class RunInfoHolder {
    public TextView pubTimeView;

    public RunInfoHolder(View view) {
        pubTimeView = view.findViewById(R.id.time);
    }
}
