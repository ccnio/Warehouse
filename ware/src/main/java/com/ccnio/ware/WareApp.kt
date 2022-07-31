package com.ccnio.ware

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.ccino.business.BusinessComponent
import com.ccnio.ware.third.koin.appModules
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

/**
 * Created by jianfeng.li on 2017/12/29.
 */
lateinit var app: Application

@HiltAndroidApp
class WareApp : MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Log.d("WareApp", "attachBaseContext: ")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("WareApp", "onCreate: ")
        app = this

        // Start Koin
        /**
         * KoinApplication 配置 Koin 注入的入口，有一下 API 可使用：
        startKoin { } ：创建一个 Koin 容器并将之注册到 GlobalContext 中，如此可以使用 GlobalContext 的 API 。
        logger( ) ：选择使用什么 Log 方式，Koin 提供了 3 种默认 Logger ，分别是 AndroidLogger 、PrintLogger 、EmptyLogger ，它们都继承自抽象类 Logger ，如不配置默认使用 EmptyLogger ，即不打印。
        modules( ) ：配置 Koin 模块并将之注入到 Koin 容器。
        properties() ：使用 HashMap 注入属性，供全局查询修改。
        fileProperties( ) ：使用给定 properties 文件注入属性，文件需要放在 src/main/resources 目录下。
        environmentProperties( ) ：注入系统、环境属性，通过 java.lang.System 注入。
         */
        startKoin {
            androidLogger()
            androidContext(this@WareApp)//传Application对象,这样注入的类中可以直接使用app
            androidFileProperties()//默认名字为koin.properties,也可以直接重新设置名称
            fragmentFactory()//fragment 对象注入, 麻烦
            modules(appModules)
        }
        //其它业务库的koin module 初始化，必须在startKoin之后
        BusinessComponent().init()//todo 其它业务的初始化
    }

}