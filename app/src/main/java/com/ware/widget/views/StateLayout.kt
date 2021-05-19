package com.ware.widget.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ware.R
import com.ware.tool.gone
import com.ware.tool.invisible
import com.ware.tool.visible
import kotlinx.android.synthetic.main.layout_state.view.*

/**
 * Created by jianfeng.li on 19-12-26.
 */

class StateLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var contentView: View? = null
    private var stateView: View? = null
    private var loadingView: View? = null
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) throw RuntimeException("StateLayout should be one child")
        if (childCount == 1) contentView = getChildAt(0)
    }

    @JvmOverloads
    fun showEmpty(@StringRes descRes: Int = R.string.common_data_empty, desStr: String? = null) = setStateView(descRes, desStr, R.drawable.data_empty_view)

    @JvmOverloads
    fun showNoNet(@StringRes descRes: Int = R.string.common_hint_network_unavailable, desStr: String? = null, listener: OnClickListener? = null) = setStateView(descRes, desStr, R.drawable.wifi_icon, listener)

    @JvmOverloads
    fun showError(@StringRes descRes: Int = R.string.common_hint_unkonwn_error, desStr: String? = null, listener: OnClickListener? = null) = setStateView(descRes, desStr, R.drawable.data_empty_view, listener)

    @JvmOverloads
    fun showLoading(@StringRes desc: Int = R.string.common_loading, desStr: String? = null) {
        if (loadingView == null) {
            loadingView = inflater.inflate(R.layout.layout_state_loading, this, false)
            addView(loadingView, 0)
        }
        loadingView?.let {
            if (desStr.isNullOrEmpty()) it.stateView.setText(desc)
            else it.stateView.text = desStr

            it.visible()
        }
        stateView?.gone()
        contentView?.invisible()
    }

    fun showContent() {
        loadingView?.gone()
        stateView?.gone()
        contentView?.visible()
    }

    private fun setStateView(@StringRes descRes: Int, desStr: String? = null, @DrawableRes icon: Int, listener: OnClickListener? = null) {
        if (stateView == null) {
            stateView = inflater.inflate(R.layout.layout_state, this, false)
            addView(stateView)
        }
        contentView?.invisible()
        loadingView?.gone()
        stateView?.let {
            it.visible()
            if (desStr.isNullOrEmpty()) it.stateView.setText(descRes)
            else it.stateView.text = desStr
            it.stateView.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
            if (listener != null) {
                it.actionView.visible()
                it.actionView.setOnClickListener(listener)
            } else {
                it.actionView.gone()
            }
        }
    }
}