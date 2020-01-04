package com.ware.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.ware.R

/**
 * Created by jianfeng.li on 19-12-5.
 */

const val STATE_LOADING = 1
const val STATE_FAIL = 2
const val STATE_DONE = 3
const val STATE_NO_MORE = 4

class FooterView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mStateView: TextView

    fun updateState(status: Int) {
        when (status) {
            STATE_LOADING -> {
                updateState(status, R.string.footer_loading)
            }
            STATE_FAIL -> {
                updateState(status, R.string.footer_fail)
            }
            STATE_DONE -> {
                updateState(status, R.string.footer_done)

            }
            STATE_NO_MORE -> {
                updateState(status, R.string.footer_no_more)
            }
        }
    }

    fun updateState(status: Int, @StringRes textRes: Int) {
        when (status) {
            STATE_LOADING -> {
                loading(textRes)
            }
            STATE_FAIL -> {
                fail(textRes)
            }
            STATE_DONE -> {
                done()
            }
            STATE_NO_MORE -> {
                noMore()
            }
        }
    }

    private fun noMore(@StringRes text: Int = R.string.footer_no_more) {
        mStateView.setText(text)
    }

    private fun done(@StringRes text: Int = R.string.footer_done) {
        mStateView.setText(text)
    }

    private fun fail(@StringRes text: Int = R.string.footer_fail) {
        mStateView.setText(text)
    }

    private fun loading(@StringRes text: Int = R.string.footer_loading) {
        mStateView.setText(text)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_footer, this, true)
        setBackgroundColor(Color.BLUE)
        mStateView = findViewById(R.id.mStateView)
        val set = ConstraintSet()
        set.clone(this)
        set.centerHorizontally(R.id.mStateView, ConstraintSet.PARENT_ID)
        set.centerVertically(R.id.mStateView, ConstraintSet.PARENT_ID)
        set.applyTo(this)
    }
}