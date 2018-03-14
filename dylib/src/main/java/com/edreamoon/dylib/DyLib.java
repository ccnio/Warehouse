package com.edreamoon.dylib;

import android.content.Context;
import android.util.Log;


/**
 * Created by jianfeng.li on 2018/3/12.
 */

public class DyLib {
    public static void test(Context context) {
        Log.e("lijf", "test: dev");
        context.getResources().getString(R.string.APP_KEY_2);
    }
}
