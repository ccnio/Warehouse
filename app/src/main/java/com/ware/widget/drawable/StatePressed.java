package com.ware.widget.drawable;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 按下效果实现策略
 * Created by GuDong on 2017/3/16 10:26.
 * Contact with ruibin.mao@moji.com.
 */

public class StatePressed {
    @IntDef({ALPHA, DARK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Way{}

    /**
     * 按下改变透明度
     */
    public static final int ALPHA = 0;
    /**
     * 按下增加黑色遮罩
     */
    public static final int DARK = 1;

}
