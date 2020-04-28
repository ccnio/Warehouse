package com.ware.util

import android.view.View

/**
 * Created by jianfeng.li on 20-4-28.
 */

/**
 * two ways to avoid double click
 */
var viewClickFlag = false
var clickRunnable = Runnable { viewClickFlag = false }
fun View.click(action: (view: View) -> Unit) {
    setOnClickListener {
        if (!viewClickFlag) {
            viewClickFlag = true
            action(it)
        }
        removeCallbacks(clickRunnable)
        postDelayed(clickRunnable, 350)
    }
}

fun View.setSafeListener(action: (view: View) -> Unit) {
    var lastClick = 0L
    setOnClickListener {
        val gap = System.currentTimeMillis() - lastClick
        lastClick = System.currentTimeMillis()
        if (gap < 1500) return@setOnClickListener
        action.invoke(it)
    }
}

fun View.visible(): View {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
    return this
}

inline fun View.visibleIf(condition: () -> Boolean): View {
    if (visibility != View.VISIBLE && condition()) visibility = View.VISIBLE
    return this
}

fun View.invisible(): View {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
    return this
}

inline fun View.invisibleIf(condition: () -> Boolean): View {
    if (visibility != View.INVISIBLE && condition()) visibility = View.INVISIBLE
    return this
}

fun View.gone(): View {
    if (visibility != View.GONE) visibility = View.GONE
    return this
}

inline fun View.goneIf(condition: () -> Boolean): View {
    if (visibility != View.GONE && condition()) visibility = View.GONE
    return this
}