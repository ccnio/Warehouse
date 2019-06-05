package com.ware.kt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ware.R
import kotlinx.android.synthetic.main.fragment_content.*

//import kotlinx.android.synthetic.main.fragment_content.view.*

class ContentFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_content, container, false)
//        view.mTextView.text = "Fragment Find View 测试"
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mTextView.text = "Fragment Find View 测试" //或者在 onCreateView 上述使用方式
    }
}
