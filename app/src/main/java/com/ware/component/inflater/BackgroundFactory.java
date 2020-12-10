package com.ware.component.inflater;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ware.R;

import org.jetbrains.annotations.NotNull;

public class BackgroundFactory implements LayoutInflater.Factory2 {

    private LayoutInflater.Factory mViewCreateFactory;
    private LayoutInflater.Factory2 mViewCreateFactory2;

    @Override
    public View onCreateView(@NotNull String name, @NotNull Context context, @NotNull AttributeSet attrs) {
        View view = null;

        //防止与其他调用factory库冲突，例如字体、皮肤替换库，用已经设置的factory来创建view
        if (mViewCreateFactory2 != null) {
            view = mViewCreateFactory2.onCreateView(name, context, attrs);
            if (view == null) {
                view = mViewCreateFactory2.onCreateView(null, name, context, attrs);
            }
        } else if (mViewCreateFactory != null) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayoutExtAttrs);

        Log.d("InflaterActivity", "onCreateView: name = " + name + "; view = " + view);

        CharSequence[] str = typedArray.getTextArray(R.styleable.LayoutExtAttrs_deviceTypeStr);
        if (str != null)
            Log.d("InflaterActivity", "onCreateView: str =" + str[0] + " " + str[1] + "; view = " + name);
        typedArray.recycle();

        if ((view instanceof TextView) && str != null) ((TextView) view).setText(str[0]);
        return view;
    }


    public void setInterceptFactory(LayoutInflater.Factory factory) {
        mViewCreateFactory = factory;
    }

    public void setInterceptFactory2(LayoutInflater.Factory2 factory) {
        mViewCreateFactory2 = factory;
    }

    @Override
    public View onCreateView(View parent, @NotNull String name, @NotNull Context context, @NotNull AttributeSet attrs) {
        View view = null;
        //防止与其他调用factory库冲突，例如字体、皮肤替换库，用已经设置的factory来创建view
        if (mViewCreateFactory2 != null) {
            view = mViewCreateFactory2.onCreateView(name, context, attrs);
            if (view == null) {
                view = mViewCreateFactory2.onCreateView(null, name, context, attrs);
            }
        } else if (mViewCreateFactory != null) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }

        Log.d("InflaterActivity", "******************88 name = " + name + "; view = " + view);
        return view;
    }
}
