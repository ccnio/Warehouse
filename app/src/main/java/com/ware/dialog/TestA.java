package com.ware.dialog;

import android.view.View;

import com.ccnio.mdialog.MDialog_Builder;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 * Created by jianfeng.li on 2021/6/13.
 */
class TestA {

    void test(){
        new MDialog_Builder.Builder(null).setOnViewClick(new Function2<View, MDialog_Builder, Unit>() {
            @Override
            public Unit invoke(View view, MDialog_Builder mDialog) {
                return null;
            }
        });
    }
}
