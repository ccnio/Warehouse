package com.ccino.ware.jetpack.room

import androidx.room.*

/**
 * Created by ccino on 2021/10/12.
 */
@Entity
class Machine(var name: String, @PrimaryKey var no: String)

@Dao
interface MachineDao {
    @Query("select * from machine")
    fun getAll(): Machine

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(machine: Machine)
}