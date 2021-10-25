package com.ccino.ware.jetpack.room

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityRoomBinding
import com.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * # 默认情况下Database不可以在主线程中进行调用的，主线程调用会崩溃。如果需要在主线程调用则使用allowMainThreadQueries进行说明。
 * # 升级相关
 * 1. 添加表需要升级
 * # 结合 flow
 * 可以返回 flow,返回的 flow 上下方是子线程，所以使用时不需要再在切到子线程
 */
private const val TAG_L = "RoomActivity"

class RoomActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val bind by viewBinding(ActivityRoomBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        bind.addView.setOnClickListener { add() }
        bind.queryView.setOnClickListener { query() }
        bind.fieldView.setOnClickListener { queryField() }
        bind.fieldsView.setOnClickListener { queryFields() }
    }

    private fun query() {
        launch {
            val user = RoomDb.roomDb.userDao().getUser("li@ccnio.com")
            user.collect { Log.d(TAG_L, "query: ret = ${it.email}:${it.address}${Thread.currentThread().name}") }

            RoomDb.roomDb.machineDao().getAll()
        }
    }

    private fun queryField() {
        launch {
            val user = RoomDb.roomDb.userDao().getFirstNameByFirstName("li")
            user.collect { Log.d(TAG_L, "query: ret = $it ${Thread.currentThread().name}") }
        }
    }

    private fun queryFields() {
        launch {
            val user = RoomDb.roomDb.userDao().getUserFullName("li@ccnio.com")
            user.collect { Log.d(TAG_L, "query: ret = $it ${Thread.currentThread().name}") }
        }
    }

    private fun add() {
        launch {
            withContext(Dispatchers.IO) {
                val user = UserEntity("li@ccnio.com", "li", "si", 22)
                val address = arrayListOf(Address("sh", 1111), Address("hn", 7777))
                user.address = address
                val insert = RoomDb.roomDb.userDao().insert(user)
                val insert2 = RoomDb.roomDb.userDao().insert(UserEntity("lii@ccnio.com", "li", "wu", 22))
                Log.d(TAG_L, "normalAction: insert ret = $insert; $insert2")
            }
        }
    }
}