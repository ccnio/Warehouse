package com.ware.jetpack.hilt

import android.os.Bundle
import android.util.Log
import com.ware.R
import com.ware.component.BaseActivity

/**
 * # HiltAndroidApp
 * All apps that use Hilt must contain an Application class that is annotated with @HiltAndroidApp.
 * HiltAndroidApp triggers Hilt's code generation, including a base class for your application that serves as the application-level dependency container.
 *
 * # AndroidEntryPoint
 * 其会创建一个依赖容器，该容器遵循Android类的生命周期
 *
 * # Inject
 * 用来注入的字段，其类型不能为Private.如果要告诉 Hilt 如何提供相应类型的实例，需要将 @Inject 添加到要注入的类的构造函数中，Hilt有关如何提供不同类型的实例的信息也称为 绑定。
 */
private const val TAG = "HiltActivity"

class HiltActivity : BaseActivity(R.layout.activity_hilt) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

//class  DataSource @Inject onstructor(){
//    fun face() {
//        Log.d(TAG, "DataSource face")
//    }
//}