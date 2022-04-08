//package com.ccino.store
//
//import kotlinx.coroutines.*
//
///**
// * Created by ccino on 2022/3/3.
// */
///*
// * Copyright 2016-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
// */
//
//@file:JvmMultifileClass
//@file:JvmName("BuildersKt")
//@file:OptIn(ExperimentalContracts::class)
//import kotlinx.coroutines.internal.*
//import kotlinx.coroutines.intrinsics.*
//import kotlinx.coroutines.selects.*
//import kotlin.contracts.*
//import kotlin.coroutines.*
//import kotlin.coroutines.intrinsics.*
//import kotlin.jvm.*
//
//// --------------- launch ---------------
//
///**
// * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
// * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
// *
// * The coroutine context is inherited from a [CoroutineScope]. Additional context elements can be specified with [context] argument.
// * If the context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
// * The parent job is inherited from a [CoroutineScope] as well, but it can also be overridden
// * with a corresponding [context] element.
// *
// * By default, the coroutine is immediately scheduled for execution.
// * Other start options can be specified via `start` parameter. See [CoroutineStart] for details.
// * An optional [start] parameter can be set to [CoroutineStart.LAZY] to start coroutine _lazily_. In this case,
// * the coroutine [Job] is created in _new_ state. It can be explicitly started with [start][Job.start] function
// * and will be started implicitly on the first invocation of [join][Job.join].
// *
// * Uncaught exceptions in this coroutine cancel the parent job in the context by default
// * (unless [CoroutineExceptionHandler] is explicitly specified), which means that when `launch` is used with
// * the context of another coroutine, then any uncaught exception leads to the cancellation of the parent coroutine.
// *
// * See [newCoroutineContext] for a description of debugging facilities that are available for a newly created coroutine.
// *
// * @param context additional to [CoroutineScope.coroutineContext] context of the coroutine.
// * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
// * @param block the coroutine code which will be invoked in the context of the provided scope.
// **/
//@InternalCoroutinesApi
//public fun CoroutineScope.launch(
//    context: CoroutineContext = EmptyCoroutineContext,
//    start: CoroutineStart = CoroutineStart.DEFAULT,
//    block: suspend CoroutineScope.() -> Unit
//): Job {
//    val newContext = newCoroutineContext(context)
//    val coroutine = if (start.isLazy)
//        LazyStandaloneCoroutine(newContext, block) else
//        StandaloneCoroutine(newContext, active = true)
//    coroutine.start(start, coroutine, block)
//    return coroutine
//}
