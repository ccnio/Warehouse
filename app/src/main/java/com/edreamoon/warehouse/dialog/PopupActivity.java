package com.edreamoon.warehouse.dialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.edreamoon.warehouse.R;

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

    private static final String TAG = "PopupActivity";
    private PopupWindow mPopWindow;
    private View mAnchorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.dismiss).setOnClickListener(this);
        mAnchorView = findViewById(R.id.anchor);
    }

    private void showPopupWindow() {
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popup, null);

            //一定要指定 PopupWindow 的宽高，否则无法显示
            mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopWindow.setContentView(contentView);
        }
//        mPopWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 500, 500);
        mPopWindow.showAsDropDown(mAnchorView, -100, 100);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show:
                showPopupWindow();
                try {
                    Log.e("lijf", "onClick: " +  DESUtil.encrypt("48113805"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.dismiss:
                mPopWindow.dismiss();

                try {
                    Log.e("lijf", "onClick: " +  DESUtil.decode("8fbc96KEFongUzo1qGl2wg=="));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
