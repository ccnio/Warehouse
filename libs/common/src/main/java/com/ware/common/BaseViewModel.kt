package com.ware.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by jianfeng.li on 19-6-24.
 */

open class BaseViewModel : ViewModel() {
    protected val mDisposables by lazy { CompositeDisposable() }

    protected fun addDispose(dispose: Disposable) {
        mDisposables.add(dispose)
    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }
}