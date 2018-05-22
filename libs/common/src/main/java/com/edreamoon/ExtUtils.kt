package com.edreamoon

import android.content.Context
import androidx.core.net.toUri

fun Context.getS(): String {

    var s = "bcd"
    s.toUri()
    return "getS"
}