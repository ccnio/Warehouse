package com.ware.binder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ware.R;

public class BinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BinderActivity.this, RecActivity.class);
                Bundle bundle = new Bundle();
                int[] array = new int[1024 * 1024];
                bundle.putIntArray("array", array);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
