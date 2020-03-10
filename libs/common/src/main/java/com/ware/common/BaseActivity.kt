package com.ware.common

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity

open class BaseActivity(@LayoutRes private val layout: Int = 0) : FragmentActivity(layout)
