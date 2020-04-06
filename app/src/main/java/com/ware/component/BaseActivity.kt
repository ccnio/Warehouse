package com.ware.component

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.ware.component.permissionutil.PermissionResult
import com.ware.component.permissionutil.PermissionUtils

open class BaseActivity(@LayoutRes private val layout: Int = 0) : FragmentActivity(layout)
