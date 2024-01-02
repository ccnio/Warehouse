package com.ccino.demo.basis

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.demo.ui.widget.Label
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

private const val TAG = "PatternActivity"

class PatternActivity : ComponentActivity() {


    interface IUserApi {
        fun login(id: String, password: String): Boolean
    }

    private fun dynamicProxy() {
        val proxyInstance = Proxy.newProxyInstance(IUserApi::class.java.classLoader, arrayOf(IUserApi::class.java),
            object : InvocationHandler {
                override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
                    // 此处打印 proxy 会报 StackOverflowError
                    // 因为打印 $proxy/$proxyInstance 就是调用代理对象的 toString() 方法，所以会一直调用 invoke()
                    Log.d(TAG, "proxy invoke: proxy = , method = $method, args = $args")
                    return getBasicValue(method.returnType)
                }
            }) as IUserApi

        val ret = proxyInstance.login("cc", "123")
        Log.d(TAG, "Proxy: instance=${proxyInstance::class.java}, ret=$ret") //$Proxy1 Unit
    }

    private fun getBasicValue(clazz: Class<*>): Any? {
        return when (clazz) {
            Int::class.java, Int::class.javaPrimitiveType -> 0
            Boolean::class.java, Boolean::class.javaPrimitiveType -> false
            Float::class.java, Float::class.javaPrimitiveType -> 0f
            Unit::class.java -> Unit
            else -> null
        }
    }

    @Composable
    fun Case() {
        Column {
            Row {
                Label("动态代理") { dynamicProxy() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Case() } }
    }
}
