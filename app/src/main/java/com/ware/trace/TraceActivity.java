package com.ware.trace;

import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ware.R;

public class TraceActivity extends AppCompatActivity {
    int count = 0;
    long longCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);
        Debug.startMethodTracing(Environment.getExternalStorageDirectory().getPath() + "/mtrace");

        //线程1
        new Thread(new Runnable() {
            @Override
            public void run() {
                printNum();
            }
        }, "printNum_thread").start();
//        线程2
        new Thread(new Runnable() {
            @Override
            public void run() {
                calculate();
            }
        }, "calculate_thread").start();
        lite();
        calculate22();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Debug.stopMethodTracing();
    }

    private void printNum() {
        for (int i = 0; i < 20000; i++) {
            print();
        }
    }

    /**
     * 模拟一个自身占用时间不长，但调用却非常频繁的函数
     */
    private void print() {
        count = count++;
    }

    /**
     * 模拟一个调用次数不多，但每次调用却需要花费很长时间的函数
     */
    private void calculate() {
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {

                for (int l = 0; l < 1000; l++) {
                    if (longCount > 10) {
                        longCount = -10;
                    }
                }

            }
        }
        Log.e("Main2Activity", String.valueOf(longCount));
    }

    private void calculate22() {
        for (int j = 0; j < 1000; j++) {

            for (int l = 0; l < 1000; l++) {
                if (longCount > 10) {
                    longCount = -10;
                }
            }

        }
        Log.e("Main2Activity", String.valueOf(longCount));
    }

    private void lite() {
        for (int j = 0; j < 500; j++) {

            for (int l = 0; l < 1000; l++) {
                if (longCount > 10) {
                    longCount = -10;
                }
            }

        }
        Log.e("Main2Activity", String.valueOf(longCount));
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TraceActivity.class);
        context.startActivity(intent);
    }
}
