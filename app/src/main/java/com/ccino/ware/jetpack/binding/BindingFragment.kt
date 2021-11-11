package com.ccino.ware.jetpack.binding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ware.R
import com.ware.databinding.FragmentBindingBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * Created by ccino on 2021/11/5.
 */
private const val TAG_L = "BindingFragment"

@AndroidEntryPoint
class BindingFragment : Fragment(R.layout.fragment_binding) {
    @Inject lateinit var httpClient: OkHttpClient

    private lateinit var binding: FragmentBindingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG_L, "onCreateView: $httpClient, fragment = $this, lifecycle = $viewLifecycleOwner")
        binding = FragmentBindingBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = BindingAdapter()
        return binding.root
    }
}