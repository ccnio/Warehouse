package com.ccnio.ware.view.statelayout

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.GravityInt
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.ccnio.ware.R

/**
 * Created by jianfeng.li on 2022/8/15.
 */
const val ALIGN_TOP = 0x01
const val ALIGN_BOTTOM = 0x02
const val ALIGN_CENTER = 0x04

@IntDef(ALIGN_TOP, ALIGN_BOTTOM, ALIGN_CENTER)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Align

private const val STATE_LOADING = 1
private const val STATE_EMPTY = 2
private const val STATE_ERROR = 3
private const val STATE_CONTENT = 4

class PageStateLayout @JvmOverloads constructor(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrSet, defStyle) {


    private var errorIcon: Int
    private var errorDesc: String
    private var errorActionDesc: String
    private var onErrorClick: (() -> Unit)? = null
    private var emptyIcon: Int
    private var emptyDesc: String
    private var emptyActionDesc: String
    private var onEmptyClick: (() -> Unit)? = null
    private var loadingDesc: String
    private var yOffset: Int
    private var gravity: Int
    private val inflater = LayoutInflater.from(context)
    private val stateViewMap = ArrayMap<Int, View>()
    private val elementViewMap = mutableMapOf<Int, View>()

    init {
        val attrs = context.obtainStyledAttributes(attrSet, R.styleable.PageStateLayout)
        errorDesc = attrs.getString(R.styleable.PageStateLayout_psl_errorDesc)
            ?: context.getString(R.string.psl_error_desc)
        errorActionDesc = attrs.getString(R.styleable.PageStateLayout_psl_errorDesc)
            ?: context.getString(R.string.psl_action_retry)
        errorIcon = attrs.getResourceId(
            R.styleable.PageStateLayout_psl_errorIcon,
            R.drawable.psl_icon_error
        )
        emptyDesc = attrs.getString(R.styleable.PageStateLayout_psl_emptyDesc)
            ?: context.getString(R.string.psl_empty_desc)
        emptyActionDesc = attrs.getString(R.styleable.PageStateLayout_psl_emptyActionDesc)
            ?: context.getString(R.string.psl_action_retry)
        emptyIcon =
            attrs.getResourceId(R.styleable.PageStateLayout_psl_emptyIcon, R.drawable.psl_icon_empty)
        loadingDesc = attrs.getString(R.styleable.PageStateLayout_psl_loadingDesc)
            ?: context.getString(R.string.psl_loading_desc)
        yOffset = attrs.getDimensionPixelSize(R.styleable.PageStateLayout_psl_alignOffset, 0)
        gravity = matchGravity(attrs.getInt(R.styleable.PageStateLayout_psl_align, ALIGN_CENTER))
        attrs.recycle()
    }

    private fun matchGravity(bias: Int) = when (bias) {
        ALIGN_BOTTOM -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        ALIGN_TOP -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
        else -> Gravity.CENTER
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) error("PageStateLayout should be only one direct child")
        if (childCount == 1) {
            stateViewMap[STATE_CONTENT] = getChildAt(0)
        }
    }

    private fun getOrCreateView(state: Int): View {
        if (state == STATE_CONTENT) {
            return requireNotNull(stateViewMap[STATE_CONTENT])
        }
        val view = stateViewMap.getOrPut(state) {//1. collect view firstly
            val layoutId = when (state) {
                STATE_LOADING -> R.layout.psl_layout_loading
                STATE_ERROR -> R.layout.psl_layout_error
                else -> R.layout.psl_layout_empty
            }
            inflater.inflate(layoutId, this, false).apply {
                adjustGravity(this, gravity, yOffset)
                addView(this, layoutParams)
            }
        }
        bindViewData(state)//2. set data
        return view
    }

    private fun bindViewData(state: Int) {
        when (state) {
            STATE_LOADING -> {
                getElementView<TextView>(STATE_LOADING, R.id.sl_loading_desc_view)?.text =
                    loadingDesc
            }
            STATE_ERROR -> {
                setIcon(STATE_ERROR, errorIcon, R.id.sl_error_desc_view)
                getElementView<TextView>(STATE_ERROR, R.id.sl_error_desc_view)?.text = errorDesc
                getElementView<View>(
                    STATE_ERROR,
                    R.id.sl_error_action_view
                )?.setOnClickListener { onErrorClick?.invoke() }
            }
            else -> {
                setIcon(STATE_EMPTY, emptyIcon, R.id.sl_empty_desc_view)
                getElementView<TextView>(STATE_EMPTY, R.id.sl_empty_desc_view)?.text = emptyDesc
            }
        }
    }

    fun inject(view: View) {
        view.visibility = View.GONE
        stateViewMap[STATE_CONTENT] = view
        this.background = ColorDrawable(Color.GRAY)
        if (view.parent == null) {
            addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        } else {
            val preContentParent = view.parent as ViewGroup
            val preContentParams = view.layoutParams
            val index = preContentParent.indexOfChild(view)
            preContentParent.removeView(view)
            addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            preContentParent.addView(this, index, preContentParams)
        }
    }

    fun inject(activity: Activity) =
        inject((activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0))

    fun inject(fragment: Fragment) = inject(requireNotNull(fragment.view))

    fun setGravity(@Align gravity: Int = this.gravity, yOffset: Int = this.yOffset) {
        this.yOffset = yOffset
        this.gravity = gravity
        stateViewMap.forEach { (t, u) ->
            if (t != STATE_CONTENT) {
                adjustGravity(u, gravity, yOffset)
            }
        }
    }

    private fun adjustGravity(view: View, @GravityInt gravity: Int, yOffset: Int) {
        (view.layoutParams as LayoutParams).apply {
            this.gravity = gravity
            topMargin = yOffset
        }
    }

    fun showEmpty(
        desc: String = context.getString(R.string.psl_loading_desc),
        @DrawableRes iconRes: Int = 0,
        actionDesc: String? = null,
        action: (() -> Unit)? = null
    ) {
        setViewState(getOrCreateView(STATE_EMPTY))
        if (desc != emptyDesc) {
            emptyDesc = desc
            getElementView<TextView>(STATE_EMPTY, R.id.sl_empty_desc_view)?.text = desc
        }
        if (actionDesc != null && actionDesc != emptyActionDesc) {
            emptyActionDesc = actionDesc
            getElementView<TextView>(STATE_ERROR, R.id.sl_empty_action_view)?.text = desc
        }
        if (iconRes > 0 && iconRes != emptyIcon) {
            emptyIcon = iconRes
            setIcon(STATE_EMPTY, emptyIcon, R.id.sl_empty_desc_view)
        }
        if (action != onEmptyClick) onEmptyClick = action
        val actionView = getElementView<View>(STATE_EMPTY, R.id.sl_empty_action_view)
        if (action == null) actionView?.visibility = View.GONE
        else {
            actionView?.visibility = View.VISIBLE
            actionView?.setOnClickListener { action() }
        }
    }

    fun showError(
        desc: String = context.getString(R.string.psl_error_desc),
        @DrawableRes iconRes: Int = R.drawable.psl_icon_error,
        @StringRes actionDesc: Int = R.string.psl_action_retry,
        action: (() -> Unit)? = null
    ) {
        setViewState(getOrCreateView(STATE_ERROR))
        if (desc != errorDesc) {
            errorDesc = desc
            getElementView<TextView>(STATE_ERROR, R.id.sl_error_desc_view)?.text = desc
        }
        val actionStr = context.getString(actionDesc)
        if (actionStr != errorActionDesc) {
            errorActionDesc = actionStr
            getElementView<TextView>(STATE_ERROR, R.id.sl_error_action_view)?.text = actionStr
        }
        if (iconRes > 0 && iconRes != errorIcon) {
            errorIcon = iconRes
            setIcon(STATE_ERROR, errorIcon, R.id.sl_error_desc_view)
        }

        if (action != onErrorClick) onErrorClick = action
        val actionView = getElementView<View>(STATE_ERROR, R.id.sl_error_action_view)
        if (action == null) actionView?.visibility = View.GONE
        else {
            actionView?.visibility = View.VISIBLE
            actionView?.setOnClickListener { action() }
        }
    }

    fun showNetError(
        @StringRes desc: Int = R.string.psl_net_error_desc,
        @DrawableRes iconRes: Int = R.drawable.psl_icon_error_net,
        @StringRes actionDesc: Int = R.string.psl_action_retry,
        action: (() -> Unit)? = null
    ) = showError(context.getString(desc), iconRes, actionDesc, action)


    fun showLoading(desc: String = context.getString(R.string.psl_loading_desc)) {
        setViewState(getOrCreateView(STATE_LOADING))
        loadingDesc = desc
        getElementView<TextView>(STATE_LOADING, R.id.sl_loading_desc_view)?.text = loadingDesc
    }

    fun showContent() = setViewState(getOrCreateView(STATE_CONTENT))

    private fun setViewState(view: View) = stateViewMap.forEach {
        it.value.visibility = if (view != it.value) View.GONE else View.VISIBLE
    }

    private inline fun <reified T : View> getElementView(state: Int, id: Int): T? {
        val stateView = stateViewMap[state] ?: return null
        return elementViewMap.getOrPut(id) { stateView.findViewById(id) } as T
    }

    private fun setIcon(state: Int, resId: Int, viewId: Int) =
        getElementView<TextView>(state, viewId)
            ?.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0)

}
