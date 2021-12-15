package com.ccino.business

import com.ccnio.business.export.IApi
import javax.inject.Inject

/**
 * Created by ccino on 2021/12/7.
 */
class ApiImpl @Inject constructor() : IApi {
    @Inject lateinit var request: Request
    override fun getName(): String {
        return "ApiImpl $request"
    }
}