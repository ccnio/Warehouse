package com.ccnio.ware.kt

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Created by jianfeng.li on 2022/9/18.
 */

private const val TAG = "AndroidScope"

class AndroidScope(
    lifecycle: Lifecycle? = null,
    lifeEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : CoroutineScope {
    private var catch: (AndroidScope.(Throwable) -> Unit)? = null
    private var finally: (AndroidScope.(Throwable?) -> Unit)? = null
    private val exceptionHandler =
        CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
            catch(throwable)
        }
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + exceptionHandler

    init {
        lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == lifeEvent) cancel()
            }
        })
    }

    open fun launch(block: suspend CoroutineScope.() -> Unit) =
        apply { launch(EmptyCoroutineContext) { block() }.invokeOnCompletion { finally(it) } }

    fun catch(block: AndroidScope.(Throwable) -> Unit = {}) = apply { catch = block }

    fun finally(block: AndroidScope.(Throwable?) -> Unit = {}) = apply { finally = block }

    private fun catch(e: Throwable) {
        catch?.invoke(this@AndroidScope, e) ?: e.printStackTrace()
    }

    private fun finally(e: Throwable?) {
        finally?.invoke(this@AndroidScope, e)
    }
}