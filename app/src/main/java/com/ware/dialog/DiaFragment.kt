package com.ware.dialog

import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ware.R
//import com.ware.img.then
//import com.ware.img.thus

/**
 * Created by jianfeng.li on 2018/1/12.
 */

class DiaFragment : DialogFragment() {
    /**
     * 如屏幕旋转时onCreateView, onCreate等会被调用，edittext内容会被清空。而普通的Dialog则直接消失不会重建
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        Log.e("lijf", "onCreateView: ")
        return inflater.inflate(R.layout.layout_dia_fragment, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MDialog);
        Log.e("lijf", "onCreate: ")

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.e("lijf", "onDismiss: ")
    }
}
