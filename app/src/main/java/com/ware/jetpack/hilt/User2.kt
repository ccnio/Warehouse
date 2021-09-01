package com.ware.jetpack.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by ccino on 2021/9/1.
 */
const val USER_NAME = "UserName"
const val USER_AGE = "UserAge"

class User2 @Inject constructor(@Named(USER_NAME) val title: String, @Named(USER_AGE) val age: Int) {
    override fun toString(): String {
        return "User2(title='$title', age=$age)"
    }
}

@Module
@InstallIn(ActivityComponent::class)
private object UserModule {
    @Named(USER_NAME)
    @Provides
    fun testNamed(): String {
        return "zhang san"
    }

    @Named(USER_AGE)
    @Provides
    fun testAge(): Int {
        return 22
    }
}