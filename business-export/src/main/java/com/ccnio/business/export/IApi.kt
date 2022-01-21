package com.ccnio.business.export

//import com.ware.common.Utils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Created by ccino on 2021/12/7.
 */
interface IApi {
    fun getName(): String
}

//val api = EntryPointAccessors.fromApplication(Utils.mContext, ApiPoint::class.java).getApi()

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ApiPoint {
    fun getApi(): IApi
}