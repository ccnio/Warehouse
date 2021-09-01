package com.ware.jetpack.hilt

import javax.inject.Inject

/**
 * Created by ccino on 2021/8/31.
 */
class GasEngine @Inject constructor() : Engine {
    override fun start() {
        println("gas engine start")
    }
}