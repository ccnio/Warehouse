package com.ccnio.ware.third.koin

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

/**
 * 普通类注入对象
 */
class Repository {
    private val appData: AppData by inject(AppData::class.java)
    fun queryName(id: String): String {
        return "cc@$id, appData = $appData"
    }
}

class Repository2 : KoinComponent {
    private val appData by inject<AppData>()
    val appD1 by inject<AppData>()//懒加载模式
    val appD2 = get<AppData>()//非懒加载模式
    fun queryName(id: String): String {
        return "cc2@$id, appData = $appData"
    }
}