package com.ware.systip.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edreamoon.Utils;
import com.ware.R;

import java.util.ArrayList;

public class RecyclerDividerActivity extends AppCompatActivity {

    private static final String TAG = "AcActivity";
    public static final int REQ_SEC = 0x2;

    private Context mAppCtx = RecyclerDividerActivity.this;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        /**
         * 第一种
         */
//        int spacingInPixels = (int) Utils.dp2px(10);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        /**
         * 第二种  修改VariedDrawable 适配此情形
         * 但最后一条底部会有分隔线
         */
//        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.custom_divider));
//        mRecyclerView.addItemDecoration(divider);

//        DividerItemDecoration decoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
//        decoration.setDrawable(ContextCompat.getDrawable(this,R.drawable.custom_divider));
//        RecycleViewDivider viewDivider = new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.custom_divider);
        RecyclerDivider divider = new RecyclerDivider((int) Utils.dp2px(10), Color.RED);
        mRecyclerView.addItemDecoration(divider);

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            strings.add("pos: " + i);
        }
        mRecyclerView.setAdapter(new DividerAdapter(strings, this));
    }
}
