package com.ware.widget.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.ware.R
import kotlinx.android.synthetic.main.layout_state.view.*
import java.util.*

private const val TAG = "StateLayout"

/**
 * state view id should be unique
 */
class StateLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var mEmptyView: View? = null
    private var mErrorView: View? = null
    private var mLoadingView: View? = null
    private var mNoNetworkView: View? = null
    private var mContentView: View? = null
    private val mEmptyViewResId: Int
    private val mErrorViewResId: Int
    private val mLoadingViewResId: Int
    private val mNoNetViewResId: Int
    private val inflater = LayoutInflater.from(context)
    private var mContentViewResId: Int
    private val stateViewIds = mutableSetOf<Int>()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StateLayout, defStyleAttr, 0)
        mEmptyViewResId = a.getResourceId(R.styleable.StateLayout_emptyView, R.layout.layout_state_empty)
        mErrorViewResId = a.getResourceId(R.styleable.StateLayout_errorView, R.layout.layout_state_error)
        mLoadingViewResId = a.getResourceId(R.styleable.StateLayout_loadingView, R.layout.layout_state_loading)
        mNoNetViewResId = a.getResourceId(R.styleable.StateLayout_noNetView, R.layout.layout_state_no_net)
        mContentViewResId = a.getResourceId(R.styleable.StateLayout_contentView, NULL_RESOURCE_ID)
        a.recycle()

        val view = inflater.inflate(R.layout.layout_state, this, true) as ViewGroup
//        val set = ConstraintSet()
//        set.clone(this)
//        set.addToVerticalChain(R.id.stateView_, ConstraintSet.PARENT_ID, R.id.actionView_)
//        set.applyTo(this)


        for (i in 0 until childCount) {
            stateViewIds.add(view.getChildAt(i).id)
        }
//        id = R.id.state_view
    }

    /**
     * 获取当前状态
     *
     * @return 视图状态
     */
    var viewStatus = -1
        private set
    private var mOnRetryClickListener: OnClickListener? = null
    private var mViewStatusListener: OnViewStatusChangeListener? = null
    private val mOtherIds = ArrayList<Int>()
    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d(TAG, "init: count = $childCount")
        if (childCount > stateViewIds.size + 1) throw RuntimeException("StateLayout should be one child")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clear(mEmptyView!!, mLoadingView!!, mErrorView!!, mNoNetworkView!!)
        if (mOtherIds.isNotEmpty()) {
            mOtherIds.clear()
        }
        if (null != mOnRetryClickListener) {
            mOnRetryClickListener = null
        }
        if (null != mViewStatusListener) {
            mViewStatusListener = null
        }
    }

    /**
     * 设置重试点击事件
     *
     * @param onRetryClickListener 重试点击事件
     */
    fun setOnRetryClickListener(onRetryClickListener: OnClickListener?) {
        mOnRetryClickListener = onRetryClickListener
    }

    @JvmOverloads
    fun showEmpty(@StringRes descId: Int = R.string.common_data_empty, descStr: String? = null, @DrawableRes icon: Int = R.drawable.data_empty_view) {
        if (mEmptyViewResId > 0) {

        }
        showStateView(descId, descStr, icon)
    }

    fun showContent() {
        setChildVisible(true)
    }

    @JvmOverloads
    fun showNoNet(@StringRes desc: Int = R.string.common_hint_network_unavailable, descStr: String? = null, @DrawableRes icon: Int = R.drawable.wifi_icon, @StringRes action: Int = 0) {
        showStateView(desc, descStr, icon, action)
    }

    fun showLoading(@StringRes desc: Int = R.string.common_loading, descStr: String? = null, @DrawableRes icon: Int = R.drawable.data_empty_view) {
        showStateView(desc, descStr, icon)
    }

    @JvmOverloads
    fun showError(@StringRes desc: Int = R.string.common_hint_unkonwn_error, descStr: String? = null, @DrawableRes icon: Int = R.drawable.data_empty_view, listener: OnClickListener? = null,
                  @StringRes action: Int = 0) {
        showStateView(desc, descStr, icon, action, listener)
    }

    private fun showStateView(@StringRes desc: Int, descStr: String? = null, @DrawableRes icon: Int, @StringRes action: Int = 0, listener: OnClickListener? = null) {
        if (descStr == null) stateView_.setText(desc)
        else stateView_.text = descStr

        stateView_.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
        setChildVisible(false)

        if (listener != null) {
            actionView_.setOnClickListener(listener)
            actionView_.setText(if (action != 0) action else R.string.common_retry)
        } else {
            actionView_.visibility = View.GONE
        }
    }

    private fun setChildVisible(showContent: Boolean) {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val isStateView = stateViewIds.contains(view.id)
            when {
                isStateView -> view.visibility = if (showContent) View.GONE else View.VISIBLE
                showContent -> view.visibility = View.VISIBLE
                else -> view.visibility = View.GONE
            }
        }
    }

    /**
     * 显示空视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    fun showEmpty(view: View?, layoutParams: ViewGroup.LayoutParams?) {
        checkNull(view, "Empty view is null.")
        checkNull(layoutParams, "Layout params is null.")
        changeViewStatus(STATUS_EMPTY)
        if (null == mEmptyView) {
            mEmptyView = view
            val emptyRetryView = mEmptyView!!.findViewById<View>(R.id.empty_retry_view)
            if (null != mOnRetryClickListener && null != emptyRetryView) {
                emptyRetryView.setOnClickListener(mOnRetryClickListener)
            }
            mOtherIds.add(mEmptyView!!.id)
            addView(mEmptyView, 0, layoutParams)
        }
        showViewById(mEmptyView!!.id)
    }

    /**
     * 显示错误视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
//    /**
//     * 显示错误视图
//     */
//    @JvmOverloads
//    fun showError(layoutId: Int = mErrorViewResId, layoutParams: ViewGroup.LayoutParams? = DEFAULT_LAYOUT_PARAMS) {
//        showError(if (null == mErrorView) inflateView(layoutId) else mErrorView, layoutParams)
//    }
//
//    /**
//     * 显示错误视图
//     *
//     * @param view         自定义视图
//     * @param layoutParams 布局参数
//     */
//    fun showError(view: View?, layoutParams: ViewGroup.LayoutParams?) {
//        checkNull(view, "Error view is null.")
//        checkNull(layoutParams, "Layout params is null.")
//        changeViewStatus(STATUS_ERROR)
//        if (null == mErrorView) {
//            mErrorView = view
//            val errorRetryView = mErrorView!!.findViewById<View>(R.id.error_retry_view)
//            if (null != mOnRetryClickListener && null != errorRetryView) {
//                errorRetryView.setOnClickListener(mOnRetryClickListener)
//            }
//            mOtherIds.add(mErrorView!!.id)
//            addView(mErrorView, 0, layoutParams)
//        }
//        showViewById(mErrorView!!.id)
//    }

    /**
     * 显示加载中视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    fun showLoading(view: View?, layoutParams: ViewGroup.LayoutParams?) {
        checkNull(view, "Loading view is null.")
        checkNull(layoutParams, "Layout params is null.")
        changeViewStatus(STATUS_LOADING)
        if (null == mLoadingView) {
            mLoadingView = view
            mOtherIds.add(mLoadingView!!.id)
            addView(mLoadingView, 0, layoutParams)
        }
        showViewById(mLoadingView!!.id)
    }

    /**
     * 显示无网络视图
     *
     * @param hintResId  自定义提示文本内容
     * @param formatArgs 占位符参数
     */
    fun showNoNetwork(hintResId: Int, vararg formatArgs: Any?) {
        showNoNetwork()
        setStatusHintContent(mNoNetworkView, hintResId, *formatArgs as Array<out Any>)
    }

    /**
     * 显示无网络视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    /**
     * 显示无网络视图
     */
    @JvmOverloads
    fun showNoNetwork(layoutId: Int = mNoNetViewResId, layoutParams: ViewGroup.LayoutParams? = DEFAULT_LAYOUT_PARAMS) {
        showNoNetwork(if (null == mNoNetworkView) inflateView(layoutId) else mNoNetworkView, layoutParams)
    }

    /**
     * 显示无网络视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    fun showNoNetwork(view: View?, layoutParams: ViewGroup.LayoutParams?) {
        checkNull(view, "No network view is null.")
        checkNull(layoutParams, "Layout params is null.")
        changeViewStatus(STATUS_NO_NETWORK)
        if (null == mNoNetworkView) {
            mNoNetworkView = view
            val noNetworkRetryView = mNoNetworkView!!.findViewById<View>(R.id.no_network_retry_view)
            if (null != mOnRetryClickListener && null != noNetworkRetryView) {
                noNetworkRetryView.setOnClickListener(mOnRetryClickListener)
            }
            mOtherIds.add(mNoNetworkView!!.id)
            addView(mNoNetworkView, 0, layoutParams)
        }
        showViewById(mNoNetworkView!!.id)
    }

    private fun setStatusHintContent(view: View?, resId: Int, vararg formatArgs: Any) {
        checkNull(view, "Target view is null.")
        setStatusHintContent(view, view!!.context.getString(resId, *formatArgs))
    }

    private fun setStatusHintContent(view: View?, hint: String) {
        checkNull(view, "Target view is null.")
        val hintView = view!!.findViewById<TextView>(R.id.status_hint_content)
        if (null != hintView) {
            hintView.text = hint
        } else {
            throw NullPointerException("Not find the view ID `status_hint_content`")
        }
    }

    private fun inflateView(layoutId: Int): View {
        return inflater.inflate(layoutId, null)
    }

    private fun showViewById(viewId: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            view.visibility = if (view.id == viewId) View.VISIBLE else View.GONE
        }
    }

    private fun showContentView() {
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            view.visibility = if (mOtherIds.contains(view.id)) View.GONE else View.VISIBLE
        }
    }

    private fun checkNull(`object`: Any?, hint: String) {
        if (null == `object`) {
            throw NullPointerException(hint)
        }
    }

    private fun clear(vararg views: View) {
        if (null == views) {
            return
        }
        try {
            for (view in views) {
                view?.let { removeView(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 视图状态改变接口
     */
    interface OnViewStatusChangeListener {
        /**
         * 视图状态改变时回调
         *
         * @param oldViewStatus 之前的视图状态
         * @param newViewStatus 新的视图状态
         */
        fun onChange(oldViewStatus: Int, newViewStatus: Int)
    }

    /**
     * 设置视图状态改变监听事件
     *
     * @param onViewStatusChangeListener 视图状态改变监听事件
     */
    fun setOnViewStatusChangeListener(onViewStatusChangeListener: OnViewStatusChangeListener?) {
        mViewStatusListener = onViewStatusChangeListener
    }

    /**
     * 改变视图状态
     *
     * @param newViewStatus 新的视图状态
     */
    private fun changeViewStatus(newViewStatus: Int) {
        if (viewStatus == newViewStatus) {
            return
        }
        if (null != mViewStatusListener) {
            mViewStatusListener!!.onChange(viewStatus, newViewStatus)
        }
        viewStatus = newViewStatus
    }

    private fun setContentViewResId(contentViewResId: Int) {
        mContentViewResId = contentViewResId
        mContentView = inflater.inflate(mContentViewResId, null)
        addView(mContentView, 0, DEFAULT_LAYOUT_PARAMS)
    }

    private fun setContentView(contentView: ViewGroup) {
        mContentView = contentView
        addView(mContentView, 0, DEFAULT_LAYOUT_PARAMS)
    }

    companion object {
        private val DEFAULT_LAYOUT_PARAMS = LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT)
        var STATUS_CONTENT = 0x00
        var STATUS_LOADING = 0x01
        var STATUS_EMPTY = 0x02
        var STATUS_ERROR = 0x03
        var STATUS_NO_NETWORK = 0x04
        private const val NULL_RESOURCE_ID = -1
        fun attach(fragment: Fragment?, rootAnchor: Int): StateLayout? {
            require(!(null == fragment || fragment.view == null)) { "fragment is null or fragment.getView is null" }
            if (-1 != rootAnchor) {
                val contentAnchor = fragment!!.requireView().findViewById<ViewGroup>(rootAnchor)
                contentAnchor?.let { attach(it) }
            }
            val contentParent = fragment!!.requireView().parent as ViewGroup
            return attach(contentParent)
        }

        fun attach(activity: Activity, rootAnchor: Int): StateLayout? {
            if (-1 != rootAnchor) {
                val contentAnchor = activity.findViewById<ViewGroup>(rootAnchor)
                contentAnchor?.let { attach(it) }
            }
            val defaultAnchor = activity.findViewById<ViewGroup>(android.R.id.content)
            return attach(defaultAnchor)
        }

        fun attach(rootAnchor: ViewGroup?): StateLayout? {
            requireNotNull(rootAnchor) { "root Anchor View can't be null" }
            val parent = rootAnchor.parent as ViewGroup
            val anchorIndex = parent.indexOfChild(rootAnchor)
            if (-1 != anchorIndex) {
                parent.removeView(rootAnchor)
                val statusView = StateLayout(rootAnchor.context)
                statusView.setContentView(rootAnchor)
                val p = rootAnchor.layoutParams
                parent.addView(statusView, anchorIndex, p)
                return statusView
            }
            return null
        }
    }
}