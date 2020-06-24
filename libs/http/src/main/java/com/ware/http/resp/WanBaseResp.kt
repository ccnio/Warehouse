package com.ware.http.resp

import com.ware.http.base.BaseResp

/**
 * Created by jianfeng.li on 20-6-23.
 */
open class WanBaseResp(private val errorCode: Int = 0, private val errorMsg: String? = null) : BaseResp() {

    override fun oK(): Boolean {
        return errorCode == 0
    }

    override fun getCode() = errorCode


    override fun getMsg() = errorMsg
}