package com.ware.jetpack.hilt

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ccino on 2021/8/31.
 */
private const val TAG = "HiltViewModelL"

@HiltViewModel
class HiltViewModel @Inject constructor() : ViewModel() {
    @Inject lateinit var repository: Repository
//
//    init {
//        Log.d(TAG, "repository = $repository")
//    }

    fun genData() {
        Log.d(TAG, "genData: $repository")
    }

}