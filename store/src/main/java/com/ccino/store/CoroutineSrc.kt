package com.ccino.store

import android.util.Log
import kotlinx.coroutines.*

/**
 * Created by ccino on 2022/3/1.
 */
private const val TAG = "Demo"

class Demo {
    fun getSiteInfo() {
        val scope = MainScope()
        scope.launch {
            Log.d(TAG, "getSiteInfo: before")
            val user = getUserInfo()
            val friendList = getFriendList(user)
            Log.d(TAG, "getSiteInfo: after $friendList, $user")
        }
        Log.d(TAG, "getSiteInfo: end")
    }

    private suspend fun getFriendList(user: String) {
        Log.d(TAG, "getFriendList: user = $user")
        delay(100)
        "Tom, Jack"
    }

    private suspend fun getUserInfo() = withContext(Dispatchers.IO) {
        Log.d(TAG, "getUserInfo: ")
        "BoyCoder"
    }

    //warning: Redundant 'suspend' modifier
    private suspend fun fakeSuspend(user: String): Int {
        Log.d(TAG, "fakeSuspend: $user")
        delay(100)
        return 22
    }
}