package com.ccino.demo.inject

import android.util.Log
import com.ccino.ksp.ExtractorInterface

@ExtractorInterface("IAClass")
class AClass : Parent() {
    fun funA() {
        Log.d("TAG", "testStr2:2 ")
    }
}