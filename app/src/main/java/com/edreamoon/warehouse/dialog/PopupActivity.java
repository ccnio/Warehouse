package com.edreamoon.warehouse.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.edreamoon.Utils;
import com.edreamoon.warehouse.R;
import com.edreamoon.warehouse.systip.ThirdActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class PopupActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PopupActivity";
    private PopupWindow mPopWindow;
    private View mAnchorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.dismiss).setOnClickListener(this);
        mAnchorView = findViewById(R.id.anchor);
        new RecyclerView(this).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    int measuredHeight = 0;//测量得到的textview的高
    int measuredWidth = 0;//测量得到的textview的宽

    private void showPopupWindow() {
        View contentView = null;
        if (mPopWindow == null) {
            contentView = LayoutInflater.from(this).inflate(R.layout.layout_redleaves_city_tip, null);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            contentView.measure(widthSpec, heightSpec);
            measuredHeight = contentView.getMeasuredHeight();
            measuredWidth = contentView.getMeasuredWidth();

            //一定要指定 PopupWindow 的宽高，否则无法显示
            mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopWindow.setContentView(contentView);
        }
//        mPopWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 500, 500);
        Log.d(TAG, measuredHeight + ":" + measuredWidth);
        mPopWindow.showAsDropDown(mAnchorView, -(int) Utils.dp2px(216), -mAnchorView.getHeight() - measuredHeight); //(int) Utils.dp2px(46)
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show:
//                showPopupWindow();
//                try {
//                    Log.e("lijf", "onClick: " + DESUtil.encrypt("48113805"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Intent intent = new Intent(this, ThirdActivity.class);
                startActivity(intent);
                break;
            case R.id.dismiss:
                mPopWindow.dismiss();

                try {
                    Log.e("lijf", "onClick: " + DESUtil.decode("8fbc96KEFongUzo1qGl2wg=="));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: pop");
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged pop: " + hasFocus);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + ": " + resultCode + ":" + data);
    }
}
