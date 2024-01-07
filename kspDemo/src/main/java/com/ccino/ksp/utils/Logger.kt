package com.ccino.ksp.utils

import com.google.devtools.ksp.processing.KSPLogger

object Logger {
    private lateinit var logger: KSPLogger
    fun init(logger: KSPLogger) {
        this.logger = logger
    }

    fun i(tag: String, msg: String) {
        logger.info("$tag: $msg")
    }
}