package com.ware.widget.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Created by jianfeng.li on 2020/3/21.
 */
public class FollowBehavior extends CoordinatorLayout.Behavior<TextView> {
    private static final String TAG = "FollowBehavior";

    public FollowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 用来确定交互行为中的dependent view，本例中当dependency是Button类的实例时返回true，
     * 就可以让系统知道布局文件中的Button就是本次交互行为中的dependent view。
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull TextView child, @NonNull View dependency) {
        Log.d(TAG, "layoutDependsOn: " + dependency);
        return dependency instanceof Button;
    }

    /**
     * dependent view发生变化时被调用，child相当于交互行为中的观察者，观察者可以在这个方法中对被观察者的变化做出响应，从而完成一次交互行为。
     */
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull TextView child, @NonNull View dependency) {
        child.setX(dependency.getX() + 150);
        child.setY(dependency.getY() + 150);
        return true;
    }
}
