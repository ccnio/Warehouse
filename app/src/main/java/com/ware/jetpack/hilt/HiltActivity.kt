package com.ware.jetpack.hilt

import android.os.Bundle
import android.util.Log
import com.ware.R
import com.ware.component.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * https://juejin.cn/post/6902009428633698312
 * # HiltAndroidApp
 * 所有使用 Hilt 的应用都必须包含一个带有 @HiltAndroidApp 注释的 Application 类。@HiltAndroidApp 会触发 Hilt 的代码生成操作，生成的代码包括应用的一个基类，该基类充当应用级依赖项容器。
 * 生成的这一 Hilt 组件会附加到 Application 对象的生命周期，并为其提供依赖项。此外，它也是应用的父组件，这意味着，其他组件可以访问它提供的依赖项。
 * 生成 Hilt_MyApplication类，这是此注解生成类之一，它是hilt组建全局的管理者。
 *
 * # 将依赖项注入 Android 类
 * 1. @AndroidEntryPoint Hilt 为带有此注释的类提供依赖项,为HiltActivity添加后,生成 Hilt_HiltActivity(HiltActivity的父类). 会为项目中的每个 Android 类生成一个单独的 Hilt 组件。
 * 2. 使用 @AndroidEntryPoint 为某个类添加注释还必须为依赖于该类的 Android 类添加注释。例如，如果您为某个 Fragment 添加注释，则还必须为使用该 Fragment 的所有 Activity 添加注释
 * 3. @AndroidEntryPoint 不能以写在抽象类上
 *
 * # 注入
 * 1. @Inject 如需从组件获取依赖项，请使用此注释执行字段注入. @Inject AnalyticsAdapter analytics;
 *
 * # 定义Hilt绑定
 * @Inject 为了执行字段注入，Hilt需要知道如何从相应组件提供必要依赖项的实例，向Hilt提供绑定信息的一种方法是构造函数注入。@Inject constructor(AnalyticsService service)
 *
 * #
 *
 *
 * # Inject
 * 用来注入的字段，其类型不能为Private.如果要告诉 Hilt 如何提供相应类型的实例，需要将 @Inject 添加到要注入的类的构造函数中，Hilt有关如何提供不同类型的实例的信息也称为 绑定。
 */
private const val TAG = "HiltActivity"

@AndroidEntryPoint
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