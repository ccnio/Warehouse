package com.ware.systip.recyclerview;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ware.R;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "AcActivity";
    public static final int REQ_SEC = 0x2;

    private Context mAppCtx = RecyclerViewActivity.this;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        DividerItemDecoration decoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.custom_divider));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            strings.add("pos: " + i);
        }
        mRecyclerView.setAdapter(new StaggeredAdapter(strings));
    }
}
