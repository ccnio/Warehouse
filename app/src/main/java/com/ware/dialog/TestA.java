package com.ware.dialog;

import android.view.View;

import com.ccnio.mdialog.DialogHolder;
import com.ccnio.mdialog.MDialog;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 * Created by jianfeng.li on 2021/6/13.
 */
class TestA {

    void test(){
        new MDialog.Builder(null).setOnViewClick(new Function2<View, MDialog, Unit>() {
            @Override
            public Unit invoke(View view, MDialog mDialog) {
                return null;
            }
        });
    }
}
