package com.ccino.demo.compose.sdk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class NormalUser(var name: String, var age: Int)

data class DataUser(val name: String, val age: Int)

/**
 * Stable 一般用于非 data class
 */
@Stable
class StableUserCorrect(
    name: String,
    age: Int
) {
    var name by mutableStateOf(name)
    var age by mutableStateOf(age)
}

@Composable
fun StateCase(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        StateDemo()
//        FunctionExample()
    }
}


/**
 * 是否刷新，一般根据前后对象 equals 判断
 */
@Composable
fun StateDemo() {
    Column(
        Modifier
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        // ========== 1. 普通 class（无 @Stable）==========
        var normalUser by remember { mutableStateOf(NormalUser("Tom", 10)) }
        Text("NormalUser: ${normalUser.name}, age: ${normalUser.age}")
        Button(onClick = {
            normalUser.name = "Jack"   // 🔥 UI 不会更新（无重组）
        }) {
            Text("修改 NormalUser.name（UI 不更新）")
        }

        // ========== 3. data class（不可变）==========
        var dataUser by remember { mutableStateOf(DataUser("Tom", 10)) }
        Text("DataUser: ${dataUser.name}, age: ${dataUser.age}")
        Button(onClick = {
            dataUser = dataUser.copy(name = "Jack")  // 🔥 UI 会更新（因为 equals 不同）
        }) {
            Text("更新 data class copy（UI 会更新）")
        }

        // 4. stable class（内部字段是 state）
        var stableUserCorrect by remember { mutableStateOf(StableUserCorrect("Tom", 10)) }
        Text("✔ StableUserCorrect（会刷新）: ${stableUserCorrect.name}")
        Button(onClick = {
            stableUserCorrect.name = "Jack"  // ✔ UI 会刷新
        }) {
            Text("修改 StableUserCorrect.name")
        }
    }
}