package com.ccino.demo.ksp

import android.util.Log
import com.ccino.ksp.extract.ExtractorInterface

@ExtractorInterface("IAClass")
class AClass : Parent() {
    fun funA() {
        Log.d("TAG", "testStr2:2 ")
    }
}