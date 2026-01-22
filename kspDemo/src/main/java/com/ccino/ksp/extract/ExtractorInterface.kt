package com.ccino.ksp.extract

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ExtractorInterface(val name: String)
