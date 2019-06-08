package com.ware.systip.recyclerview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ware.R;
import com.ware.face.DisplayUtil;
import com.ware.face.FaceDecor;

import java.util.ArrayList;

public class RecyclerDividerActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
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
//        RecyclerDivider divider = new RecyclerDivider((int) Utils.dp2px(10), Color.RED);
        FaceDecor decor = new FaceDecor(DisplayUtil.dip2px(20), 0, false);
        mRecyclerView.addItemDecoration(decor);

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            strings.add("pos: " + i);
        }
        mRecyclerView.setAdapter(new DividerAdapter(strings, this));
    }
}
