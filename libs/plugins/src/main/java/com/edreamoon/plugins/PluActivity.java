package com.edreamoon.plugins;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PluActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PluActivity.class);
        context.startActivity(intent);
    }
}