package com.ccnio.ware.http.resp

/**
 * Created by ccino on 2021/12/17.
 */
data class ProjectRes(
    val data: List<ProjectInfo>,
)

data class ProjectInfo(
    val author: String,
    val desc: String,
    val name: String
)

class Repo