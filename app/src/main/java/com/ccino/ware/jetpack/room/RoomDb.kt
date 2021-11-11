package com.ccino.ware.jetpack.room

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ware.WareApp

/**
 * Created by ccino on 2021/10/12.
 */
private const val TAG_L = "RoomDb"

@Database(entities = [UserEntity::class,
    Machine::class,
    TempTrigger::class],
    version = 2, exportSchema = false)
abstract class RoomDb : RoomDatabase() {

    companion object {
        private val dbCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG_L, "onCreate: ")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(TAG_L, "onOpen: ")
                trigger(db)
            }
        }

        //单例，每个RoomDatabase实例都相当消耗性能
        val roomDb = Room.databaseBuilder(WareApp.sContext, RoomDb::class.java, "room_db")
            .addCallback(dbCallback).build()

        /**
         * 监听的数据插入冲突未执行时，不会执行 trigger 的语句
         *
         *  CREATE TRIGGER // 创建触发器
        auto_remove    // 触发器名称，后期可以用来查询和移除触发器
        BEFORE         // 在事件之前触发，改为AFTER就是之后触发
        INSERT         // 在插入事件触发，还支持DELETE、UPDATE
        ON db_list_table   // 操作哪个表
        BEGIN   // 触发语句开始
        // 触发语句，删除db_list_table表中和当前插入数据的user_id、item_id相同的数据
        DELETE FROM db_list_table WHERE user_id=NEW.user_id AND item_id=NEW.item_id;// 不要忘了分号
        // 因为触发事件是INSERT，所以表单数据要用NEW.column-name引用；
        // 可能比较绕，你品品，你细品，是不是很有道理(ಡωಡ)
        END;    // 触发语句结束
        复制代码NEW 和OLD 关键字的英文文档
        Both the WHEN clause and the trigger actions may access elements of the row being inserted, deleted or updated using references of the form "NEW.column-name" and "OLD.column-name",
        where column-name is the name of a column from the table that the trigger is associated with.
        OLD and NEW references may only be used in triggers on events for which they are relevant, as follows:

        INSERT	NEW references are valid    // 插入时NEW有效
        UPDATE	NEW and OLD references are valid    // 均有效
        DELETE	OLD references are valid    // 删除时OLD有效
        // 这里的INSERT，UPADATE，DELETE指的是触发动作类型，不是触发语句类型。就是BEFOR/AFTER后面的操作


        # when 语句中也支持查询: WHEN NEW.ID NOT IN (SELECT ID FROM LOG), 不支持 if else
         */
        private val TRIGGERS = arrayOf("update",/* "DELETE",*/ "insert")
        private fun trigger(db: SupportSQLiteDatabase) {
            //val db = RoomDb.instance.openHelper.writableDatabase
            val currentTimeMillis = System.currentTimeMillis()
            TRIGGERS.forEach {
                val triggerName = "trigger_$it"
                val dropTrigger = "drop trigger if exists $triggerName"
                db.execSQL(dropTrigger)
                /**
                 * insert or ignore 无法解决冲突问题，所以增加了自增主键
                 */
                val whenCase = if (it == "delete") "when (old.age=20)" else "when (new.age=20)"
                val trigger = "create trigger $triggerName before $it on user " +
                        whenCase +
                        "begin " +
                        "insert or ignore into temp_trigger (no) values (\"trigger_no\");" +
                        "end;"
                db.execSQL(trigger)
            }
            Log.d(TAG_L, "trigger: ${System.currentTimeMillis() - currentTimeMillis}")
        }
    }

    abstract fun userDao(): UserDao
    abstract fun machineDao(): MachineDao
}