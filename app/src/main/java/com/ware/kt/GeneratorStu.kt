package com.ware.kt

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine

/**
 * Created by jianfeng.li on 20-4-12.
 */

interface Generator<T> {
    operator fun iterator(): Iterable<T>
}

class GeneratorImpl<T>(private val block: suspend GeneratorScope<T>.(T) -> Unit, private val parameter: T) :
        Generator<T> {
    override fun iterator(): Iterable<T> {
        null!!
    }

}

class GeneratorIterator<T>(private val block: suspend GeneratorScope<T>.(T) -> Unit, override val parameter: T) :
        GeneratorScope<T>(), Iterator<T>, Continuation<Any?> {
    override val context: CoroutineContext = EmptyCoroutineContext

    init {
        val coroutineBlock: suspend GeneratorScope<T>.() -> Unit = { block(parameter) }
        val start = coroutineBlock.createCoroutine(this, this)
    }

    override fun hasNext(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun next(): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun yield(value: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resumeWith(result: Result<Any?>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

abstract class GeneratorScope<T> internal constructor() { //限制yield在generator函数里用
    protected abstract val parameter: T
    abstract suspend fun yield(value: T)
}

fun <T> generator(block: suspend GeneratorScope<T>.(T) -> Unit): (T) -> Generator<T> {
    return { parameter: T ->
        GeneratorImpl(block, parameter)
    }
}

fun main() {
    val nums = generator { start: Int ->
        for (i in 0..5) {
            yield(start + i)
        }
    }

//    val seq = nums(10)
//    for (j in seq) {
//        println(j)
//    }
}