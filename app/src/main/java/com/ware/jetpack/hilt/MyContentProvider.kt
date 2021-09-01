package com.ware.jetpack.hilt

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

/**
 * Created by ccino on 2021/8/31.
 */
private const val TAG = "HiltContentProvider"

class MyContentProvider : ContentProvider() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MyEntryPoint {
        fun getRetrofit(): Retrofit
    }

    override fun onCreate(): Boolean {
        context?.let {
            val appContext = it.applicationContext
            val entryPoint = EntryPointAccessors.fromApplication(appContext, MyEntryPoint::class.java)
            val retrofit = entryPoint.getRetrofit()
            Log.d(TAG, "onCreate: $retrofit")
        }
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
}