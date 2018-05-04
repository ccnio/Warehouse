package com.edreamoon.warehouse.systip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.edreamoon.warehouse.R;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private RunAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mListView = findViewById(R.id.list);

        mAdapter = new RunAdapter(this);

        View footer = LayoutInflater.from(this).inflate(R.layout.item_run_foot, null);
        mListView.addFooterView(footer);


        View header = LayoutInflater.from(this).inflate(R.layout.item_run_head, null);
        mListView.addFooterView(header);

        header.findViewById(R.id.more).setOnClickListener(this);

        mListView.setAdapter(mAdapter);
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            strings.add("this is item: " + i);
        }
        mAdapter.setData(strings);
    }

    private boolean more = true;

    @Override
    public void onClick(View v) {
        mAdapter.setMore(more);
        more = !more;
    }
}
