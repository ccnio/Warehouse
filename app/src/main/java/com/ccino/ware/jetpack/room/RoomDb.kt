package com.ccino.ware.jetpack.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ware.WareApp

/**
 * Created by ccino on 2021/10/12.
 */
@Database(entities = [UserEntity::class,
    Machine::class],
    version = 2, exportSchema = false)
abstract class RoomDb : RoomDatabase() {

    companion object {
        //单例，每个RoomDatabase实例都相当消耗性能
        val roomDb = Room.databaseBuilder(WareApp.sContext, RoomDb::class.java, "room_db").build()
    }

    abstract fun userDao(): UserDao
    abstract fun machineDao(): MachineDao

}