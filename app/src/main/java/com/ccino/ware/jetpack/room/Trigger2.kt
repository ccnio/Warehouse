package com.ccino.ware.jetpack.room

import androidx.room.*

/**
 * Created by ccino on 2021/10/12.
 */
@Entity(tableName = "trigger2")
class Trigger2(@PrimaryKey val id: Int = 0)

//@Dao
//interface TriggerDao {
//    @Query("select * from temp_trigger")
//    fun getAll(): Machine
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insert(trigger: TempTrigger)
//}