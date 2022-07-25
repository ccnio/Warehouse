package com.ccnio.ware.jetpack.nav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ccnio.ware.R

private const val TAG = "ThirdFragment"

class ThirdFragment : Fragment() {

    private val viewModel by viewModels<ThirdViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: $viewModel")
        viewModel.liveData.observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: live data = $it")
        }
        view.findViewById<View>(R.id.go_four_view).setOnClickListener {
            findNavController().navigate(R.id.nav_fourth_fragment)
        }

        view.findViewById<View>(R.id.live_view).setOnClickListener {
            viewModel.addLiveData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }
}