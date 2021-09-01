package com.ware.jetpack.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * Created by ccino on 2021/8/31.
 */
@Module
@InstallIn(ActivityComponent::class)
abstract class EngineModule {
    @BindGasEngine
    @Binds
    abstract fun bindGasEngine(gasEngine: GasEngine): Engine

    @BindElectricEngine
    @Binds
    abstract fun bindElectricEngine(electricEngine: ElectricEngine): Engine
}