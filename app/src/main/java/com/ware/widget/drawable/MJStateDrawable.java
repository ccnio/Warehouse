package com.ware.widget.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * 文档说明地址 http://gitlab.moji.com/Android-Team/WeatherMore/wikis/autobkstateutils
 * Created by GuDong on 2017/3/16 10:23.
 * Contact with ruibin.mao@moji.com.
 */

public class MJStateDrawable extends StateListDrawable {
//    private static final String TAG = "MJStateDrawable";
//    /**
//     * 默认的按下后的透明度变化值
//     */
//    private static final float DEFAULT_ALPHA_VALUE = 0.7f;
//    /**
//     * 默认按下使用 20% 透明度的黑色作为遮罩
//     */
//    private static final float DEFAULT_DARK_ALPHA_VALUE = 0.1f;
//
//
//    private MJStateDrawable() {
//        super();
//    }
//
//    public MJStateDrawable(@DrawableRes int res) {
//        this(res, getDefaultAlpha(StatePressed.ALPHA), StatePressed.ALPHA);
//    }
//
//    public MJStateDrawable(@NonNull Bitmap res) {
//        this(res, getDefaultAlpha(StatePressed.ALPHA), StatePressed.ALPHA);
//    }
//
//    public MJStateDrawable(@NonNull Drawable res) {
//        this(res, getDefaultAlpha(StatePressed.ALPHA), StatePressed.ALPHA);
//    }
//
//    public MJStateDrawable(@DrawableRes int res, @StatePressed.Way int way) {
//        this(res, getDefaultAlpha(way), way);
//    }
//
//    public MJStateDrawable(@NonNull Bitmap res, @StatePressed.Way int way) {
//        this(res, getDefaultAlpha(way), way);
//    }
//
//    public MJStateDrawable(@NonNull Drawable res, @StatePressed.Way int way) {
//        this(res, getDefaultAlpha(way), way);
//    }
//
//    public MJStateDrawable(@DrawableRes int res, @FloatRange(from = 0.0f, to = 1.0f) float alpha, @StatePressed.Way int way) {
//        this();
//        Drawable normal;
//        Drawable pressed;
//        Drawable unable;
//        try {
//            normal = Utils.getDrawable(res);
//            pressed = Utils.getDrawable(res);
//            unable = Utils.getDrawable(res);
//        } catch (Exception e) {
//            MJLogger.e(TAG, e);
//            return;
//        }
//        pressed.mutate();
//        unable.mutate();
//        setStateDrawable(alpha, way, normal, pressed, unable);
//    }
//
//    public MJStateDrawable(@NonNull Bitmap res, @FloatRange(from = 0.0f, to = 1.0f) float alpha, @StatePressed.Way int way) {
//        this();
//        Drawable normal = new BitmapDrawable(AppDelegate.getAppContext().getResources(), res);
//        Drawable pressed = new BitmapDrawable(AppDelegate.getAppContext().getResources(), res);
//        Drawable unable = new BitmapDrawable(AppDelegate.getAppContext().getResources(), res);
//        pressed.mutate();
//        unable.mutate();
//        setStateDrawable(alpha, way, normal, pressed,unable);
//    }
//
//    public MJStateDrawable(@NonNull Drawable res, @FloatRange(from = 0.0f, to = 1.0f) float alpha, @StatePressed.Way int way) {
//        this();
//        res.mutate();
//        Drawable.ConstantState constantState = res.getConstantState();
//        if (null != constantState) {
//            Drawable pressed = constantState.newDrawable();
//            pressed.mutate();
//            setStateDrawable(alpha, way, res, pressed);
//        } else {
//            setStateDrawable(alpha, way, res, res);
//        }
//    }
//
//    private void setStateDrawable(@FloatRange(from = 0.0f, to = 1.0f) float alpha, @StatePressed.Way int way, @NonNull Drawable normal, @NonNull Drawable pressed) {
//        switch (way) {
//            case StatePressed.ALPHA:
//                pressed.setAlpha(convertAlphaToInt(alpha));
//                break;
//            case StatePressed.DARK:
//                pressed.setColorFilter(alphaColor(Color.BLACK, convertAlphaToInt(alpha)), PorterDuff.Mode.SRC_ATOP);
//                break;
//            default:
//                pressed.setAlpha(convertAlphaToInt(alpha));
//        }
//        addViewStates(normal, pressed);
//    }
//
//    private void setStateDrawable(@FloatRange(from = 0.0f, to = 1.0f) float alpha, @StatePressed.Way int way, @NonNull Drawable normal, @NonNull Drawable pressed, @NonNull Drawable unable) {
//        switch (way) {
//            case StatePressed.ALPHA:
//                pressed.setAlpha(convertAlphaToInt(alpha));
//                break;
//            case StatePressed.DARK:
//                pressed.setColorFilter(alphaColor(Color.BLACK, convertAlphaToInt(alpha)), PorterDuff.Mode.SRC_ATOP);
//                break;
//            default:
//                pressed.setAlpha(convertAlphaToInt(alpha));
//        }
//        unable.setAlpha(convertAlphaToInt(0.5f));
//        if (isKitkat() && !(pressed instanceof ColorDrawable) && !(pressed instanceof GradientDrawable)) {
//            pressed = kitkatDrawable(pressed, way, alpha);
//        }
//        if (isKitkat() && !(unable instanceof ColorDrawable) && !(pressed instanceof GradientDrawable)) {
//            unable = kitkatUnableDrawable(unable);
//        }
//        addViewStates(normal, pressed, unable);
//    }
//
//    private Drawable kitkatDrawable(@NonNull Drawable pressed, @StatePressed.Way int mode, @FloatRange(from = 0.0f, to = 1.0f) float alpha) {
//        Bitmap bitmap = Bitmap.createBitmap(pressed.getIntrinsicWidth(), pressed.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas myCanvas = new Canvas(bitmap);
//        switch (mode) {
//            case StatePressed.ALPHA:
//                pressed.setAlpha(convertAlphaToInt(alpha));
//                break;
//            case StatePressed.DARK:
//                pressed.setColorFilter(alphaColor(Color.BLACK, convertAlphaToInt(alpha)), PorterDuff.Mode.SRC_ATOP);
//                break;
//        }
//        pressed.setBounds(0, 0, pressed.getIntrinsicWidth(), pressed.getIntrinsicHeight());
//        pressed.draw(myCanvas);
//        return new BitmapDrawable(AppDelegate.getAppContext().getResources(), bitmap);
//    }
//
//    private Drawable kitkatUnableDrawable(@NonNull Drawable pressed) {
//        Bitmap bitmap = Bitmap.createBitmap(pressed.getIntrinsicWidth(), pressed.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas myCanvas = new Canvas(bitmap);
//        pressed.setAlpha(convertAlphaToInt(0.5f));
//        pressed.setBounds(0, 0, pressed.getIntrinsicWidth(), pressed.getIntrinsicHeight());
//        pressed.draw(myCanvas);
//        return new BitmapDrawable(AppDelegate.getAppContext().getResources(), bitmap);
//    }
//
//
//    private Drawable getUnableStateDrawable(@NonNull Drawable unable) {
//        if (isKitkat() && !(unable instanceof ColorDrawable)) {
//            return kitkatUnableDrawable(unable);
//        }
//        unable.setAlpha(convertAlphaToInt(0.5f));
//        return unable;
//    }
//
//    private static boolean isKitkat() {
//        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
//    }
//
//
//    static float getDefaultAlpha(@StatePressed.Way int way) {
//        switch (way) {
//            case StatePressed.ALPHA:
//                return DEFAULT_ALPHA_VALUE;
//            case StatePressed.DARK:
//                return DEFAULT_DARK_ALPHA_VALUE;
//            default:
//                return DEFAULT_ALPHA_VALUE;
//        }
//    }
//
//    private void addViewStates(Drawable drawableNormal, Drawable drawablePressed) {
//        addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
//        addState(new int[]{}, drawableNormal);
//    }
//
//    private void addViewStates(Drawable drawableNormal, Drawable drawablePressed, Drawable drawableUnable) {
//        addState(new int[]{-android.R.attr.state_enabled}, drawableUnable);
//        addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
//        addState(new int[]{}, drawableNormal);
//    }
//
//    private int convertAlphaToInt(@FloatRange(from = 0.0f, to = 1.0f) float alpha) {
//        return (int) (255 * alpha);
//    }
//
//    private int alphaColor(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
//        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
//    }

}
