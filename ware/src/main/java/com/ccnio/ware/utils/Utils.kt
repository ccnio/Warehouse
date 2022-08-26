package com.ccnio.ware.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.ui.unit.dp
import com.ccnio.ware.R
import com.ccnio.ware.WareApp
import com.ccnio.ware.app
import kotlin.math.roundToInt

/**
 * Created by ccino on 2021/12/15.
 */
val <T : Context> T.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun getScreenWidth(): Int {
    val a = mutableListOf<String>()
    val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(outMetrics)
    return outMetrics.widthPixels
}
//
//inline fun <reified T : Any> Activity.intent(key: String): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
//    intent.extras?.get(key)
//}

inline fun <reified T > Activity.intent(key: String) = lazy(LazyThreadSafetyMode.NONE) {
    when (val value = intent?.extras?.get(key)) {
        null -> value
        is T -> value
        else -> throw RuntimeException("type not match")
    }
}

//crossinline 显示声明 inline 函数的形参 lambda 不能有 return 语句（可以有 return@label 语句），避免lambda 中的 return 影响外部程序流程
inline fun <reified T> Activity.intent(key: String, crossinline default: () -> T) = lazy(LazyThreadSafetyMode.NONE) {
    when (val value = intent?.extras?.get(key)) {
        null -> default.invoke()
        is T -> value
        else -> throw RuntimeException("type not match")
    }
}

private val metrics = Resources.getSystem().displayMetrics
val Number.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            metrics
        ).roundToInt()
    }

val Number.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        metrics
    ).roundToInt()