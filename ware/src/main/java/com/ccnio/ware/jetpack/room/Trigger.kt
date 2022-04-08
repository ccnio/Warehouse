package com.ccnio.ware.jetpack.room

import android.util.Log
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by ccino on 2021/12/17.
 */

/**
 * # insertIgnore/insertReplace
 * 1. 无论冲突与否, 即无论被监测的表插入是否成功，都会触发 insert trigger
 * 2. insertReplace 冲突时会先删除原来的记录再插入，所以 trigger 会同时触发 delete、insert
 */
private const val TAG_L = "Trigger_one"
private const val TRIGGER_TABLE = "trigger_one"

@Entity(tableName = TRIGGER_TABLE)
class Trigger(@PrimaryKey val id: Int = 0) {
    companion object {
        fun createTrigger(db: SupportSQLiteDatabase) {
            val triggers = arrayOf("update", "DELETE", "insert")
            val currentTimeMillis = System.currentTimeMillis()
            triggers.forEach {
                val triggerName = "trigger_$it"
                val dropTrigger = "drop trigger if exists $triggerName"
                db.execSQL(dropTrigger)
//                val whenCase = if (it == "delete") "when (old.age=20)" else "when (new.firstName='li')"
                val whenCase = ""
                val trigger = "create trigger $triggerName before $it on user " +
                        whenCase +
                        "begin " +
                        "insert or ignore into $TRIGGER_TABLE (id) values (null);" +
                        "end;"
                db.execSQL(trigger)
            }
            Log.d(TAG_L, "trigger: ${System.currentTimeMillis() - currentTimeMillis}")
        }
    }
}

@Dao
interface TriggerDao{
    @Query("insert into $TRIGGER_TABLE (id) values (null);")
    fun insert()
}