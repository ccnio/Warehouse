package com.ccnio.ware.third.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

/**
 * Created by jianfeng.li on 2022/8/31.
 */
@JsonClass(generateAdapter = true)
data class PersonBean(
    var name: String,
    var age: Int,
    var country: String = "中国",
    val sex: Sex
)

enum class Sex {
    MALE, FEMALE
}

class SexAdapter {
    @FromJson
    fun fromJson(value: Boolean): Sex {
        return if (value) Sex.MALE else Sex.FEMALE
    }

    @ToJson
    fun toJson(sex: Sex): Boolean {
        return sex == Sex.MALE
    }
}
