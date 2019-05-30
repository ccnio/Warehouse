package com.ware.dialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ware.R;

/**
 * DialogFragment 是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
 * 在手机配置变化导致 Activity 需要重新创建时，例如旋转屏幕，基于 DialogFragment 的对话框将会由 FragmentManager 自动重建，然而基于 Dialog 实现的对话框却没有这样的能力
 */

public class DialogActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Log.e("lijf", "activity onCreate: ");
        findViewById(R.id.start_dia).setOnClickListener(this);
        findViewById(R.id.start_common).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("lijf", "activity onDestroy: ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_dia:
                new DiaFragment().show(getFragmentManager(), "DiaFrag");
                break;
            case R.id.start_common:
                startDialog();
                break;
        }
    }

    private void startDialog() {
        new CommonDialog(this).show();
    }
}
