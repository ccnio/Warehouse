package com.ware.jetpack.hilt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ware.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

private const val TAG = "HiltViewModelActivityL"
@AndroidEntryPoint
class HiltViewModelActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(HiltViewModel::class.java) }
    @Inject lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilt_fragment)
        viewModel.genData()
        Log.d(TAG, "onCreate: user = ${ System.identityHashCode(user)}")
    }
}
