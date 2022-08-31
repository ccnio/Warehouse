package com.ccnio.ware.third.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Created by jianfeng.li on 2022/8/31.
 */
class BooleanAdapter {
    @FromJson
    fun fromJson(value: Int): Boolean {
        return value != 0
    }

    @ToJson
    fun toJson(value: Boolean): Int {
        return if (value) 1 else 0
    }
}