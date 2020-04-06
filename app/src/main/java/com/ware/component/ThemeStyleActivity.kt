package com.ware.component

import android.Manifest
import android.os.Bundle
import androidx.lifecycle.Observer
import com.ware.R
import com.ware.component.permissionutil.PermissionResult
import com.ware.component.permissionutil.PermissionUtils

class ThemeStyleActivity : BaseActivity(R.layout.activity_theme_style) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Day) //must before setContentView
        super.onCreate(savedInstanceState)
        PermissionUtils(this).request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .observe(this, Observer {
                 when (it) {
                        PermissionResult.Grant -> {

                        }
                        is PermissionResult.Deny -> {

                        }
                        else -> {
                        }

                    }
                })
    }
}
