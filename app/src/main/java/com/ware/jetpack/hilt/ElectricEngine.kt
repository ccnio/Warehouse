package com.ware.jetpack.hilt

import javax.inject.Inject

/**
 * Created by ccino on 2021/8/31.
 */
class ElectricEngine @Inject constructor() : Engine {
    override fun start() {
        println("electric engine start")
    }
}