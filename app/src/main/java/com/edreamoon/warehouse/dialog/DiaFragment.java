package com.edreamoon.warehouse.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edreamoon.warehouse.R;

/**
 * Created by jianfeng.li on 2018/1/12.
 */

public class DiaFragment extends DialogFragment {
    /**
     * 如屏幕旋转时onCreateView, onCreate等会被调用，edittext内容会被清空。而普通的Dialog则直接消失不会重建
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("lijf", "onCreateView: ");
        return inflater.inflate(R.layout.layout_dia_fragment, container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MDialog);
        Log.e("lijf", "onCreate: ");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("lijf", "onDismiss: ");
    }
}
