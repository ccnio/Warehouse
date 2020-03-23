package com.ware.kt

/**
 * Created by jianfeng.li on 20-3-23.
 */
data class User(val name: String) {
    constructor(name: String, age: Int) : this(name)
}