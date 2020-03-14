package com.ware.kt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ware.R
import kotlinx.android.synthetic.main.fragment_content.*

const val KEY = "key"

class ContentFragment : Fragment(R.layout.fragment_content) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = arguments?.getString(KEY)
    }
}
