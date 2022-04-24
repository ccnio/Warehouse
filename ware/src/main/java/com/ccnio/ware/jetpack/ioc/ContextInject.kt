package com.ccnio.ware.jetpack.ioc

import android.app.Activity
import android.app.Application
import android.content.Context
import com.ccnio.ware.WareApp
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

/**
 * Created by ccino on 2022/4/8.
 */
class ContextApp @Inject constructor(val application: Application)

class ContextActivity @Inject constructor(val activity: Activity)

//@ApplicationContext 参数必须是 Context 类型
class ContextApp2 @Inject constructor(@ApplicationContext val application: Context)

//@ActivityContext 参数必须是 Context 类型

class ContextActivity2 @Inject constructor(@ActivityContext val activity: Context)

@ActivityScoped
//@Singleton error
class ContextActivityScope @Inject constructor(val activity: Activity)

//自定义的application
class ContextApp3 @Inject constructor(val application: WareApp)

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    fun provideMyApplication(application: Application): WareApp {
        return application as WareApp
    }
}