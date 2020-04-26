package com.ware.widget.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_state_layout.*

private const val TAG = "StateActivity"

class StateLayoutActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_layout)
        loadingView.setOnClickListener(this)
        contentView.setOnClickListener(this)
        errorView.setOnClickListener(this)
        netView.setOnClickListener(this)
        emptyView.setOnClickListener(this)
//        statusView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loadingView -> stateView.showLoading()
            R.id.contentView -> stateView.showContent()
            R.id.errorView -> stateView.showError()
            R.id.netView -> stateView.showNoNet()
            R.id.emptyView -> {
                stateView.showEmpty()
            }
        }
    }
}
