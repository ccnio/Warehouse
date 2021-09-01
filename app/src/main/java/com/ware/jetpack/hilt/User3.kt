package com.ware.jetpack.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject
import javax.inject.Qualifier

/**
 * Created by ccino on 2021/9/1.
 */

class User3 @Inject constructor(@User3Module.QualifierTitle val title: String, @User3Module.QualifierAge val age: Int) {
    override fun toString(): String {
        return "User3(title='$title', age=$age)"
    }
}

@Module
@InstallIn(ActivityComponent::class)
private object User3Module {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class QualifierTitle

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class QualifierAge

    @QualifierTitle
    @Provides
    fun testQualifier(): String {
        return "Qualifier"
    }

    @QualifierAge
    @Provides
    fun test1Qualifier(): Int {
        return 23
    }
}