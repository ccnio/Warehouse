package com.ware.component.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.ware.R
import kotlinx.android.synthetic.main.fragment_content.*

const val KEY = "key"


class ContentFragment : Fragment(R.layout.fragment_content) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ContentFragment", "onCreate: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ContentFragment", "onResume: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ContentFragment", "onDestroy: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ContentFragment", "onStop: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d("ContentFragment", "onStart: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ContentFragment", "onPause: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = arguments?.getString(KEY)
        Log.d("ContentFragment", "onViewCreated: ")
    }

    companion object {
        fun newInstance(param1: String) =
                ContentFragment().apply {
                    arguments = Bundle().apply {
                        putString(KEY, param1)
                    }
                }
    }
}
