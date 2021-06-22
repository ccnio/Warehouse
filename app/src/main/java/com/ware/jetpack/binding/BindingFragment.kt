package com.ware.jetpack.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ware.R

/**
 * Created by jianfeng.li on 2021/5/28.
 *
 * Fragment 的存在时间比其视图长。请务必在 Fragment 的 onDestroyView() 方法中清除对绑定类实例的所有引用。比如在viewpager中滑动时，触发onDetach()时，生命周期只会走到onDestroyView()，而不会走onDestroy()，这时，为了防止内存泄露，我们需要把binding置空。
 */
class BindingFragment : Fragment(R.layout.fragment_bind) {
//    private var binding: FragmentBindBinding? = null
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = FragmentBindBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.tvFragment.text = "fragment"
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
}