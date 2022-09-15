package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityKotlinBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.ccnio.ware.utils.intent
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty1

private const val TAG = "KotlinActivity"

//定义的顶层属性
var topLevelValue = 10

sealed class Fruit {
    object Apple : Fruit()
    data class Peach(val name: String) : Fruit()
}

class KotlinActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityKotlinBinding::bind)
    private val delegateIntent by intent<String>("key")
    private val delegateIntentDefault by intent("key") { "default" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        funAsParam()
        bind.delegateView.setOnClickListener { DelegateDemo().delegate() }
        Log.d(TAG, "delegateIntent: $delegateIntent, $delegateIntentDefault")
        bind.operatorView.setOnClickListener { OperatorDemo().case() }
        bind.reflectView.setOnClickListener { reflect() }
        bind.sealedView.setOnClickListener { sealedFun() }
    }

    private fun sealedFun() {
        Log.d(TAG, "sealedFun: ${Fruit.Apple}")
        Log.d(TAG, "sealedFun: ${Fruit.Peach("mitao")}")
    }

    private fun reflect() {
        val child = ChildClass() //需要一个接收者

        /**** call方法调用函数 ****/
        val callableFunction = ChildClass::classFunction //获取该类该方法的引用
        callableFunction.call(child, 20)//调用

        /**** call方法调用属性 ****/
        val callableValue: KProperty1<ChildClass, Int> =
            ChildClass::classValue // KProperty1<ChildClass, Int>
        val value = callableValue.call(child)
        Log.d(TAG, "reflect value = $value")

        /**** call方法调用顶层属性 ****/
        val callableTopLevel: KMutableProperty0<Int> = ::topLevelValue
        Log.d(TAG, "top level value = ${callableTopLevel.call()}")


        /**** KProperty获取顶层属性的引用 ****/
        val callable: KMutableProperty0<Int> = ::topLevelValue
        callable.set(100) //调用set方法设置属性值
        Log.d(TAG, "reflect KProperty: ${callable.get()}")//调用get方法来获取值
    }


    private fun funAsParam() {
        "abc".receiver {
            Log.d(TAG, "funAsParam: ${isNullOrBlank()}")
        }
    }

    private inline fun <T> T.receiver(block: T.() -> Unit): T {
        block()
        return this
    }
}

//定义一个测试类
class ChildClass : ParentClass() {
    val classValue: Int = 20

    //它包含一个方法，只有一个参数
    fun classFunction(intValue: Int) {
        Log.d(TAG, "classFunction $intValue")
    }
}

open class ParentClass

