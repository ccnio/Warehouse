package com.ware.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.ware.R

/**
 * Created by jianfeng.li on 19-12-21.
 */

fun hasMore(status: Status): Boolean {
    return status != Status.LOADING && status != Status.DONE
}

enum class Status {
    LOADING, FAIL, MORE, DONE;
}

class FooterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mStateView: TextView

    fun updateState(status: Status) {
        when (status) {
            Status.LOADING -> {
                updateState(status, R.string.footer_loading)
            }
            Status.FAIL -> {
                updateState(status, R.string.footer_fail)
                KotlinVersion
            }
            Status.MORE -> {
                updateState(status, R.string.footer_done)

            }
            Status.DONE -> {
                updateState(status, R.string.footer_no_more)
            }
        }
    }

    fun updateState(status: Status, @StringRes textRes: Int) {
        when (status) {
            Status.LOADING -> {
                loading(textRes)
            }
            Status.FAIL -> {
                fail(textRes)
            }
            Status.MORE -> {
                done()
            }
            Status.DONE -> {
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
        LayoutInflater.from(context).inflate(R.layout.view_footer, this, true)
        mStateView = findViewById(R.id.stateView_)
        val set = ConstraintSet()
        set.clone(this)
        set.centerHorizontally(R.id.stateView_, ConstraintSet.PARENT_ID)
        set.centerVertically(R.id.stateView_, ConstraintSet.PARENT_ID)
        set.applyTo(this)
    }
}