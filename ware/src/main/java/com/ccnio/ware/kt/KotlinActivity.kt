package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityKotlinBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.ccnio.ware.utils.intent
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val TAG_L = "KotlinActivity"

class KotlinActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityKotlinBinding::bind)
    private val delegateIntent by intent<String>("key")
    private val delegateIntentDefault by intent("key") {
        "default"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        funAsParam()
        bind.delegateView.setOnClickListener { delegate() }
        Log.d(TAG_L, "delegateIntent: $delegateIntent, $delegateIntentDefault")
    }


    /**
     * Kotlin 通过关键字 by 实现委托
     * Lazy代理, Observable代理, Not Null代理，字段映射代理
     * 场景：activity、fragment参数 接收/传递
     */
    var notNullStr: String by Delegates.notNull() //notNull 适用于那些无法在初始化阶段就确定属性值的场合。注意，如果属性在赋值前就被访问的话则会抛出异常。
    private var str2 by DelegateStr() //定义在函数里时 thisRef 为空，定义为类属性时为所属类的对象引用
    var str3 by DelegateStr2() //定义在函数里时 thisRef 为空，定义为类属性时为所属类的对象引用

    class DelegateStr {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            Log.e(TAG_L, "getValue: $thisRef, delegating '【${property.name}】")
            return "getCC"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            Log.e(TAG_L, "$value assigned to '【${property.name}】 in $thisRef.'")
        }
    }

    class DelegateStr2 : ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            Log.e(TAG_L, "getValue 2222: $thisRef, delegating '【${property.name}】")
            return "getCC2222"
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            Log.e(TAG_L, "2222 $value assigned to '【${property.name}】 in $thisRef.'")
        }
    }

    private fun delegate() {
        /**
         * lazy代理 返回一个Lazy<T>实例，该实例作为代理来实现懒加载功能
         */
        val lazy = lazy(LazyThreadSafetyMode.NONE) { "lazy str" }
        val str: String by lazy

        val observableStr by Delegates.observable("初始值") { prop, old, new ->
            println("ref -> $prop, 旧值：$old -> 新值：$new")
        }

        /**
         * 自定义代理，代理的对象必须有对应的 setValue getValue 函数
         */
        //自定义代理 1
        Log.d(TAG_L, str2)
        str2 = "ccnio"
        Log.d(TAG_L, "*********************")
        //自定义代理 2
        Log.d(TAG_L, str3)
        str3 = "ccnio222"
    }


    private fun funAsParam() {
        "abc".receiver {
            Log.d(TAG_L, "funAsParam: ${isNullOrBlank()}")
        }
    }

    private inline fun <T> T.receiver(block: T.() -> Unit): T {
        block()
        return this
    }
}

