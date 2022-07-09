package com.ccnio.ware.jetpack.nav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ccnio.ware.R

private const val TAG = "NavSecondFragment"

class SecondFragment : Fragment() {

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: secHostAc = $activity， arg1=${arguments?.getBundle("KEY_1")}")

        val fragmentArgs = SecondFragmentArgs.fromBundle(requireArguments())
        Log.d(
            TAG,
            "boolean data = ${fragmentArgs.booleanData}, stringData = ${fragmentArgs.stringData}"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.go_third_view).setOnClickListener {
            findNavController().navigate(R.id.nav_third_fragment)
        }
    }
}