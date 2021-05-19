package com.ware.tool

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by jianfeng.li on 20-6-23.
 */

fun <T> io2Main(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
}

fun <T> io(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()) }
}
