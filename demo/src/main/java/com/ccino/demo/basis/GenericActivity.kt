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
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private const val TAG = "GenericActivity"

class GenericActivity : ComponentActivity() {

    private fun genericInfo() {
        val map: Map<String, Int> = object : HashMap<String, Int>() {}
        val type = map.javaClass.genericSuperclass as ParameterizedType
        val typeArguments: Array<Type> = type.actualTypeArguments

        for (typeArgument in typeArguments) {
            Log.d(TAG, "genericInfo: $typeArgument")
        }
//        class java.lang.String
//        class java.lang.Integer
    }

    @Composable
    fun Case() {
        Column {
            Row {
                Label("泛型信息获取") { genericInfo() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Case() } }
    }
}

open class Fruit
class Apple : Fruit()
class Orange : Fruit()