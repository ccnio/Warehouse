package com.ccino.ware.jetpack.room

import androidx.room.*
import com.google.gson.Gson
import com.ware.WareApp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow

/**
 * Created by ccino on 2021/10/12.
 */
/**
 * primaryKeys：复合主键
 * indices 向实体给某些列添加索引以加快查询速度，列出要在索引或复合索引中包含的列的名称
 * 字段或[字段组]必须是唯一的。unique 属性设为 true，强制实施此唯一性属性。以下代码示例可防止表格具有包含 firstName 和 lastName 列的[同一组值]的两行。
 */
@Entity(tableName = "user" /*,primaryKeys = ["email","firstName"]*/, indices = [Index(value = ["firstName", "lastName"], unique = true)])
@TypeConverters(value = [AddressConverter::class])
class UserEntity(@PrimaryKey val email: String, val firstName: String, val lastName: String, val age: Int) {
    @ColumnInfo(name = "nickName") var nick: String? = null
    @Ignore val hobby: String? = null

    @ColumnInfo(name = "location")
    var address: List<Address>? = null
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MyEntryPoint {
    fun getGson(): Gson
}

class AddressConverter {
    private val gson: Gson

    init {
        val entryPoint = EntryPointAccessors.fromApplication(WareApp.sContext, MyEntryPoint::class.java)
        gson = entryPoint.getGson()
    }

    @TypeConverter fun parseJson(value: String?): List<Address> {
        return if (value.isNullOrEmpty()) emptyList() else gson.fromJson(value, Array<Address>::class.java).toList()
    }

    @TypeConverter fun addressToJson(value: List<Address>?): String {
        return gson.toJson(value)
    }
}

data class Address(val province: String, val postCode: Int)

data class FieldsTwo(
    /* @ColumnInfo(name = "firstName")*/ var firstName: String? = null,
    /*@ColumnInfo(name = "lastName")*/ var lastName: String = ""
)

@Dao
interface UserDao {
    /**
     * OnConflictStrategy
    REPLACE:见名知意,替换,违反的记录被删除，以新记录代替之
    ignore: 违反的记录保持原貌，其它记录继续执行
    fail: 终止命令，违反之前执行的操作得到保存
    abort 终止命令，恢复违反之前执行的修改
    rollback 终止命令和事务，回滚整个事务
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: UserEntity): Long

    @Query("select * from user where email = :email")
    fun getUser(email: String): Flow<UserEntity>

    /**
     * 查询某一个字体
     * 返回值 Flow<String> 只返回查询到的第一个
     */
    @Query("select firstName from user where firstName = :firstName")
    fun getFirstNameByFirstName(firstName: String): Flow<List<String>>

    /**
     * 查询某几个字体
     */
    @Query("select firstName, lastName from user where email = :email")
    fun getUserFullName(email: String): Flow<FieldsTwo>

    @Query("select distinct firstName from user where firstName =:familyName")
    fun getFirstNames(familyName: String): Flow<List<String>>
}