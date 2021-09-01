package com.ware.jetpack.hilt

import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Created by ccino on 2021/9/1.
 */
@ActivityScoped
class User(private val name: String, private val age: Int) {
    @Inject constructor() : this("li si", 27)

    override fun toString(): String {
        return "User(name='$name', age=$age)"
    }
}