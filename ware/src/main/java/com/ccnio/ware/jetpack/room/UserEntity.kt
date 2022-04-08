package com.ccnio.ware.jetpack.room

import androidx.room.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Created by ccino on 2021/10/12.
 */
/**
 * primaryKeys：复合主键
 * indices 向实体给某些列添加索引以加快查询速度，列出要在索引或复合索引中包含的列的名称
 * 字段或[字段组]必须是唯一的。unique 属性设为 true，强制实施此唯一性属性。以下代码示例可防止表格具有包含 firstName 和 lastName 列的[同一组值]的两行。
 * 返回值： 返回的list类型可以为非空
 */
@Entity(tableName = "user" /*,primaryKeys = ["email","firstName"]*/, indices = [Index(value = ["firstName", "lastName"], unique = true)])
@TypeConverters(value = [AddressConverter::class])
data class UserEntity(@PrimaryKey val email: String, var firstName: String, val lastName: String, var age: Int) {
    @ColumnInfo(name = "nickName") var nick: String? = null
    @Ignore val hobby: String? = null

    @ColumnInfo(name = "location")
    var address: List<Address>? = null
}

//@EntryPoint
//@InstallIn(SingletonComponent::class)
//interface MyEntryPoint {
//    fun getGson(): Gson
//}

class AddressConverter {
    private val gson: Gson = Gson()
//
//    init {
//        val entryPoint = EntryPointAccessors.fromApplication(app, MyEntryPoint::class.java)
//        gson = entryPoint.getGson()
//    }

    @TypeConverter
    fun parseJson(value: String?): List<Address> {
//        return if (value.isNullOrEmpty()) emptyList() else gson.fromJson(value, Array<Address>::class.java).toList()
        return emptyList()
    }

    @TypeConverter
    fun addressToJson(value: List<Address>?): String {
        return gson.toJson(value)
    }
}

data class Address(val province: String, val postCode: Int)

data class FieldsTwo(
    /* @ColumnInfo(name = "firstName")*/ var firstName: String? = null,
    /*@ColumnInfo(name = "lastName")*/ var lastName: String = ""
)

@Dao
abstract class UserDao {
    /**
     * OnConflictStrategy
    REPLACE:见名知意,替换,违反的记录被删除，以新记录代替之
    ignore: 违反的记录保持原貌，其它记录继续执行
    fail: 终止命令，违反之前执行的操作得到保存
    abort 终止命令，恢复违反之前执行的修改
    rollback 终止命令和事务，回滚整个事务
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertConflictIgnore(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertConflictReplace(user: UserEntity): Long

    @Query("select * from user where email = :email")
    abstract suspend fun getUser(email: String): UserEntity

    @Query("select * from user")
    abstract fun getAllUser(): List<UserEntity>

    @Query("select count(*) from user")
    abstract fun getUserCount(): Flow<Int>

    @Update
    abstract fun updateUser(user: UserEntity): Int

    @Delete
    abstract fun delete(user: UserEntity): Int

    @Update
    abstract fun update(user: UserEntity)

    /**
     * 查询某一个字段
     * 返回值 Flow<String> 只返回查询到的第一个
     */
    @Query("select firstName from user where firstName = :firstName")
    abstract fun getFirstNameByFirstName(firstName: String): Flow<List<String>>

    /**
     * 查询某几个字段
     */
    @Query("select firstName, lastName from user where email = :email")
    abstract fun getUserFullName(email: String): Flow<FieldsTwo>

    @Query("select distinct firstName from user where firstName =:familyName")
    abstract fun getFirstNames(familyName: String): Flow<List<String>>

    @Query("select min(age) from user")
    abstract fun getMinAge(): Int

//    /**
//     * 必须 open
//     */
//    @Transaction
//    open fun transactionWay(user: UserEntity, machine: Machine) {
//        insert(user)
//        RoomDb.roomDb.machineDao().insert(machine)
//    }

    @Query("delete from user where email = :email")
    abstract fun delete(email: String)
}