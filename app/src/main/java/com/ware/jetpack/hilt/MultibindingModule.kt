package com.ware.jetpack.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Created by ccino on 2021/8/31.
 */
private const val TAG = "HiltNetWork"

@Module
@InstallIn(SingletonComponent::class)
class MultibindingModule {
    @Provides
    @IntoSet
    fun provideStr(): String {
        return "MultibindingModule str3"
    }
}