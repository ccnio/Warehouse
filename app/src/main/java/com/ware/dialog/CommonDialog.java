package com.ware.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.ware.R;

/**
 * Created by jianfeng.li on 2018/1/12.
 */

public class CommonDialog extends Dialog {

    public CommonDialog(@NonNull Context context) {
        this(context, 0);
    }

    public CommonDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, R.style.app_MDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dia_fragment);
    }
}
