package com.ccino.business

import org.koin.core.context.loadKoinModules
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class BusinessComponent {
    private val libModule = module {
        factoryOf(::TimeData)
    }

    fun init() {
        loadKoinModules(libModule)
    }
}


class TimeData