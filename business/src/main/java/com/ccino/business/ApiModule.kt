package com.ccino.business

import com.ccnio.business.export.IApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by ccino on 2021/12/7.
 */
@Module
@InstallIn(SingletonComponent::class) //无法保证 api 是单例
abstract class ApiModule {
    @Binds
    @Singleton //此处才是 api 单例的保证
    abstract fun bindApi(api: ApiImpl): IApi
}