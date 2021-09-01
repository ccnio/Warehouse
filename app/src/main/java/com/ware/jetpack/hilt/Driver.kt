package com.ware.jetpack.hilt

import android.app.Application
import android.content.Context
import com.ware.WareApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by ccino on 2021/8/31.
 */
class Driver @Inject constructor(app: Application, myApp: WareApp, info: DriverInfo) { //or @ApplicationContext
    //class Driver @Inject constructor(@ApplicationContext val app: Context, info: DriverInfo) {
    init {
        println("Driver: context = $app, myApp = $myApp, info = $info")
    }
}

class Driver2 @Inject constructor(@ApplicationContext val context: Context, info: DriverInfo) {
    init {
        println("Driver: context = $context, info = $info")
    }
}
