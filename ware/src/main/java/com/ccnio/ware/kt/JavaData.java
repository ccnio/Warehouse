package com.ccnio.ware.kt;

import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Created by jianfeng.li on 2023/11/21.
 */
public class JavaData {
    public static class Machine {
        public String name;
        public Info info;

//        @Override
//        public boolean equals(@Nullable Object obj) {
//            Log.d("KotlinActivity", "equals: ");
//            if (!(obj instanceof Machine)) {
//
//                return false;
//            }
//            return (this.name == ((Machine) obj).name);
//        }
    }

    public static class Info {
        public String id;
    }
}


