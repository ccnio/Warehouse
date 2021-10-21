package com.ware.jetpack.hilt

import android.app.Application
import com.google.gson.Gson
import com.ware.WareApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by ccino on 2021/8/31.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    fun provideMyApplication(application: Application): WareApp {
        return application as WareApp
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}