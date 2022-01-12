package com.ccnio.ware.jetpack.room

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityRoomBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.*

/**
 * # 默认情况下Database不可以在主线程中进行调用的，主线程调用会崩溃。如果需要在主线程调用则使用allowMainThreadQueries进行说明。
 * # 升级相关
 * 1. 添加表需要升级
 *
 * # 结合 flow
 * - 可以返回 flow,返回的 flow 上下方是子线程(被suspend修饰时也是子线程)，所以使用时不需要再在切到子线程
 * - 正常情况下collect执行完成后会执行后面代码，但 room 返回的 flow， collect 后的代码不会执行到。？？？？
 * - 如果表有变化 collect 会再次进行查询，所以可以看出 collect 一直在挂起
 * # 运算符
 * - distinct 结果去重
 * -
 */
private const val TAG_L = "RoomActivityL"

class RoomActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val userDao = RoomDb.roomDb.userDao()
    private val bind by viewBinding(ActivityRoomBinding::bind)

    //    private val machineDao = RoomDb.roomDb.machineDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        bind.insertView.setOnClickListener { insertNormal() }
        bind.insertIgnoreView.setOnClickListener { insertIgnore() }
        bind.insertReplaceView.setOnClickListener { insertReplace() }
//        bind.addView.setOnClickListener { add() }
//        bind.deleteView.setOnClickListener { delete() }
//        bind.queryView.setOnClickListener { query() }
//        bind.updateView.setOnClickListener { update() }
//        bind.fieldView.setOnClickListener { queryField() }
//        bind.operateView.setOnClickListener { operation() }
//        bind.flowView.setOnClickListener { flowAction() }
//        bind.transactionView.setOnClickListener { transaction() }
    }

    private fun insertNormal() {
        launch {
            withContext(Dispatchers.IO) {
                val idSuffix = System.currentTimeMillis() / 1000
                val user = UserEntity("li$idSuffix@ccnio.com", "li", "si$idSuffix", 20)
                val address = arrayListOf(Address("sh", 1111), Address("hn", 7777))
                user.address = address
                val insert = userDao.insertConflictIgnore(user)
                Log.d(TAG_L, "insertNormal: $insert")
            }
        }
    }

    private fun insertIgnore() {
        launch {
            withContext(Dispatchers.IO) {
                val idSuffix = System.currentTimeMillis() / 1000
                val user = UserEntity("li@ccnio.com", "li", "si$idSuffix", 20)
                val address = arrayListOf(Address("sh", 1111), Address("hn", 7777))
                user.address = address
                val insert = userDao.insertConflictIgnore(user)
                Log.d(TAG_L, "insertIgnore: $insert")
            }
        }
    }

    private fun insertReplace() {
        launch {
            withContext(Dispatchers.IO) {
                val idSuffix = System.currentTimeMillis() / 1000
                val user = UserEntity("li", "li", "si$idSuffix", 20)
                val insert = userDao.insertConflictReplace(user)
                Log.d(TAG_L, "insertReplace: $insert")
            }
        }
    }

//
//    private fun transaction() {
//        launch(Dispatchers.IO) {
//
//            val currentTimeMillis = System.currentTimeMillis()
//            val user = UserEntity("$currentTimeMillis@qq.com", "ff-$currentTimeMillis", "lic", 22)
//            val machine = Machine("name-$currentTimeMillis", "no-$currentTimeMillis")
//            /*   // 1.
//               RoomDb.roomDb.runInTransaction {
//                   userDao.insert(user)
//                   val i = 1 / 0
//                   machineDao.insert(machine)
//               }*/
//            //2.
//            userDao.transactionWay(user, machine)
//        }
//    }
//
//    private fun update() {
//        launch {
//            withContext(Dispatchers.IO) {
//                val user = userDao.getAllUser().first()
//                user.age = (System.currentTimeMillis() % 100).toInt()
//                user.firstName="zhang"
//                val updateUser = userDao.updateUser(user)
//                Log.d(TAG_L, "update: $updateUser")
//            }
//        }
//    }
//
//    private fun flowAction() {
//        launch {
////            userDao.getAllUser().collect {
////                Log.d(TAG_L, "flowAction: ${it.size}")
////            }
//
//            userDao.getUserCount().collect {
//                Log.d(TAG_L, "flowAction: $it")
//            }
////            userDao.getAllUser().conflate().collect { Log.d(TAG_L, "query: $it") }
//        }
//    }
//
//
//    private fun operation() {
////        launch {
////            val userDao = userDao
////            //min
////            withContext(Dispatchers.IO) {
////                val minAge = userDao.getMinAge()
////                Log.d(TAG_L, "operation: min = $minAge")
////            }
////
////            //distinct
////            val firsNames = userDao.getFirstNames("li")
////            firsNames.collect {
////                Log.d(TAG_L, "distinct room: $it")
////            }
////            //not reach
////            Log.d(TAG_L, "distinct: ")
////        }
//    }
//
//    private fun query() {
//        launch {
//            withContext(Dispatchers.IO) {
////            val user = userDao.getUser("li@ccnio.com")
////            Log.d(TAG_L, "query: ret = ${user.email}:${user.address}${Thread.currentThread().name}")
////            }
//                val allUser = userDao.getAllUser()
//                Log.d(TAG_L, "query: $allUser")
////            RoomDb.roomDb.machineDao().getAll()
//            }
//        }
//    }
//
//    private fun queryField() {
//        launch {
//            //查某个字段
//            val user = userDao.getFirstNameByFirstName("li")
//            user.collect { Log.d(TAG_L, "query: ret = $it ${Thread.currentThread().name}") }
//
//            //查部分字段
//            val user2 = userDao.getUserFullName("li@ccnio.com")
//            user2.collect { Log.d(TAG_L, "query: ret = $it ${Thread.currentThread().name}") }
//        }
//    }
//
//    private fun add() {
//        launch {
//            withContext(Dispatchers.IO) {
//                val idSuffix = System.currentTimeMillis() / 1000
//                val user = UserEntity("li$idSuffix@ccnio.com", "li", "si$idSuffix", 20)
//                val address = arrayListOf(Address("sh", 1111), Address("hn", 7777))
//                user.address = address
//                val insert = userDao.insert(user)
//                val insert2 = userDao.insert(UserEntity("lwu${idSuffix + 1}@ccnio.com", "li", "wu$idSuffix", 22))
////                val insert3 = userDao.insert(UserEntity("li@ccnio.com", "li", "wu", 22))
////                Log.d(TAG_L, "normalAction: insert ret = $insert; $insert2; $insert3")
//            }
//        }
//    }
//
//    private fun delete() {
//        launch {
//            withContext(Dispatchers.IO) {
//              val first = userDao.getAllUser().first()
//                userDao.delete(first.email)
////                val insert2 = userDao.insert(UserEntity("lwu${idSuffix + 1}@ccnio.com", "li", "wu$idSuffix", 22))
////                val insert3 = userDao.insert(UserEntity("li@ccnio.com", "li", "wu", 22))
////                Log.d(TAG_L, "normalAction: insert ret = $insert; $insert2; $insert3")
//            }
//        }
//    }
}