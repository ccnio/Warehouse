package com.ccino.ware.jetpack.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

/**
 * Created by ccino on 2021/10/12.
 */
@Entity
class Machine(val name: String, @PrimaryKey val no: String)

@Dao
interface MachineDao {
    @Query("select * from machine")
    fun getAll(): Machine
}