package com.ccnio.ware.third.koin

import android.app.Application
import android.util.Log

private const val TAG = "InjectBean"

//带参数的类
class User(val name: String)
class User2(val appData: AppData)


//需要注入一个 application
class AppData(var app: Application)

//多个构造函数时  如何指定构造对象
class NormalData {
    var numData: Int = 0
    var userName: String = ""
    var mApp: Application? = null
    var appData: AppData? = null

    constructor(userName: String, numData: Int) {
        this.userName = userName
        this.numData = numData
    }

    constructor(appData: AppData) {
        this.appData = appData
    }

    constructor(mApp: Application) {
        this.mApp = mApp
    }

    fun printInfo(str: String) {//打印里面的信息
        Log.d(
            TAG,
            str + "的信息    numData:" + numData + "///userName:" + userName + "///application是否为空:" + (mApp == null) + "///appData是否为空:" + (appData == null)
        )
    }
}

