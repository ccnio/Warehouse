package com.ccnio.ware.third.koin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ccnio.ware.R
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val TAG = "KoinFragment"

class KoinFragment(private val repository: Repository) : Fragment() {

    /*
     * Declare shared WeatherViewModel with WeatherActivity
     */
    private val vm by sharedViewModel<KoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "repository = $repository ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_koin, container, false)
    }

}