package com.ccnio.ware

import android.util.Log
import com.ccnio.ware.structure.IChannel

/**
 * Created by ccino on 2022/4/24.
 */
class ChannelImpl : IChannel {
    override fun doSomething() {
        Log.d("IChannel", "Google doSomething: ")
    }

}