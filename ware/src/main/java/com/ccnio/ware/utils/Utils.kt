package com.ccnio.ware.utils

import android.content.Context
import android.view.LayoutInflater

/**
 * Created by ccino on 2021/12/15.
 */
val <T : Context> T.inflater: LayoutInflater
    get() = LayoutInflater.from(this)