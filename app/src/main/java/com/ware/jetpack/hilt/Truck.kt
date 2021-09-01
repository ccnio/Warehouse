package com.ware.jetpack.hilt

import javax.inject.Inject

/**
 * Created by ccino on 2021/8/31.
 */
class Truck @Inject constructor(private val driver: Driver) { //driver: or inject by filed
    @BindGasEngine @Inject lateinit var gasEngine: Engine
    @BindElectricEngine @Inject lateinit var electricEngine: Engine
//    @Inject lateinit var driver: Driver

    fun deliver() {
        gasEngine.start()
        electricEngine.start()
        println("Truck is delivering cargo. Driven by $driver")
    }
}