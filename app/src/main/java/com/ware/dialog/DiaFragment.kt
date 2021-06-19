package com.ware.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ware.R

/**
 * Created by jianfeng.li on 2018/1/12.
 */

class DiaFragment : DialogFragment() {
    /**
     * 如屏幕旋转时onCreateView, onCreate等会被调用，edittext内容会被清空。而普通的Dialog则直接消失不会重建
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dia_fragment, container)
    }
}
