package com.ccino.demo.http.adapter

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiWrapper(val value: KClass<*>)
