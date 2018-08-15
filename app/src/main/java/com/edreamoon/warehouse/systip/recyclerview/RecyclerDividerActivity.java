package com.edreamoon.warehouse.systip.recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.edreamoon.Utils;
import com.edreamoon.warehouse.R;

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
        int spacingInPixels = (int) Utils.dp2px(10);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            strings.add("pos: " + i);
        }
        mRecyclerView.setAdapter(new DividerAdapter(strings));
    }
}
