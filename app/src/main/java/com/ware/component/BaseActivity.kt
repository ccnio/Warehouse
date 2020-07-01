package com.ware.component

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class BaseActivity(@LayoutRes private val layout: Int = 0) : FragmentActivity(layout), CoroutineScope by MainScope() {
    private val disposables by lazy { CompositeDisposable() }

    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
