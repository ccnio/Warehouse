package com.ccnio.ware.kt

/**
 * Created by jianfeng.li on 2022/9/15.
 */

data class User(var name: String, val hobby: List<String> = emptyList())

//效果同 User
class User2(var name: String, val age: Int) {
    override fun equals(other: Any?): Boolean {
        if (other !is User2) return false
        return other.name == name && other.age == age
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + age
        return result
    }
}