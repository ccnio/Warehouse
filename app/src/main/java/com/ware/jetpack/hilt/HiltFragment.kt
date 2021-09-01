package com.ware.jetpack.hilt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ware.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * Created by ccino on 2021/9/1.
 */
private const val TAG = "HiltFragmentL"

@AndroidEntryPoint
class HiltFragment : Fragment() {
    @Inject lateinit var user: User
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hilt, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:user = ${ System.identityHashCode(user)} $user")
    }
}