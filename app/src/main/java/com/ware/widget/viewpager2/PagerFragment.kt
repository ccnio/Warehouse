package com.ware.widget.viewpager2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ware.R
import kotlinx.android.synthetic.main.fragment_content.*

/**
 * Created by jianfeng.li on 20-6-16.
 */
private const val KEY_PAGER_DATE = "key_pager"
private const val KEY_PAGER_POS = "key_pos"

private const val TAG = "PagerFragment"

class PagerFragment : Fragment(R.layout.fragment_content) {
    private var data: String? = null
    private var pos = -1

    /**
     * 注意：使用requireActivity(),这样与附属activity 的ViewModelStoreOwner一致，这样就是所有Pager使用同一个ViewModel实例,达到互相通知的目的
     */
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(PagerShareVM::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = data
        sendView.setOnClickListener {
            it.postDelayed(Runnable { viewModel.data.value = data }, 2000)

//            viewModel.data.value = data
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "page $pos: received changed data = $it")
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getString(KEY_PAGER_DATE)
        pos = arguments?.getInt(KEY_PAGER_POS, -1) ?: -1

        Log.d(TAG, "$pos onCreate")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "$pos onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "$pos onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "$pos onResume")
    }

    companion object {
        fun instance(pos: Int): PagerFragment {
            Log.d(TAG, " $pos new fragment")
            return PagerFragment().apply {
                arguments = Bundle(1).apply {
                    putString(KEY_PAGER_DATE, "this is data $pos")
                    putInt(KEY_PAGER_POS, pos)
                }
            }
        }
    }
}