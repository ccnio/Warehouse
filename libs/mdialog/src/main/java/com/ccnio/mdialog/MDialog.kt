package com.ccnio.mdialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.io.Serializable

private const val SAVED_INSTANCE = "dialog_instance"
private const val TAG = "MDialog"

private const val DEFAULT_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT
private const val DEFAULT_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
private const val DEFAULT_GRAVITY = Gravity.CENTER

/**
 * # 如何限制 Params 只能 MDialog 内创建，目前只能限制在本 module 内。
 * # 必须手动处理配置变化时的恢复。因此Params需要支持序列化
 */
open class MDialog(param: Params? = null) : DialogFragment() {
    private var params: Params = param ?: this.params()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (savedInstanceState?.getSerializable(SAVED_INSTANCE) as? Params)?.let { params = it }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_INSTANCE, params)
    }

    open fun params(): Params = Params()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.run {
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(params.gravity)
            attributes.x = params.offsetX
            attributes.y = params.offsetY
            attributes = attributes
            setWindowAnimations(params.animationStyle)
        }
        params.view?.parent?.let { (it as ViewGroup).removeView(params.view) }
        return params.view ?: inflater.inflate(params.layoutRes, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(params.width, params.height)
        }
        isCancelable = params.cancelable
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        params.onDismiss?.invoke(Unit)
    }

    private fun initView(view: View) {
        val holder = DialogHolder(view, this)
        params.clickIds?.let { holder.addClickIds(it) }
        holder.setOnViewClick(params.onViewClick)
        params.onViewBind?.invoke(view)
    }

    fun show(manager: FragmentManager) {
        manager.beginTransaction().add(this, null).commitAllowingStateLoss()
    }

    override fun dismiss() = dismissAllowingStateLoss()

    class ParamBuilder {
        private val param = Params()
        fun create() = param
        fun setLayoutRes(@LayoutRes layoutRes: Int) = apply { param.layoutRes = layoutRes }
        fun setWidth(width: Int) = apply { param.width = width }
        fun setHeight(height: Int) = apply { param.height = height }
        fun setAnimationRes(@StyleRes res: Int) = apply { param.animationStyle = res }

        @JvmOverloads
        fun setGravity(gravity: Int, offsetX: Int = 0, offsetY: Int = 0) = apply {
            param.gravity = gravity
            param.offsetX = offsetX
            param.offsetY = offsetY
        }

        fun setOnDismiss(onDismiss: (Unit) -> Unit) = apply { param.onDismiss = onDismiss }
        fun setView(view: View) = apply { param.view = view }
        fun setCancelable(cancelable: Boolean) = apply { param.cancelable = cancelable }
        fun setOnViewClick(onViewClick: (View, MDialog) -> Unit) = apply { param.onViewClick = onViewClick }
        fun addClickIds(vararg clickIds: Int) = apply { param.clickIds = clickIds }
        fun setOnViewBind(onViewBind: (View) -> Unit) = apply { param.onViewBind = onViewBind }
    }

    class Params internal constructor() : Serializable {
        var tag: String? = null
        var onViewBind: ((View) -> Unit)? = null
        var onViewClick: ((View, MDialog) -> Unit)? = null
        var clickIds: IntArray? = null

        var view: View? = null
        var offsetX = 0
        var offsetY = 0
        var gravity = DEFAULT_GRAVITY
        var height = DEFAULT_HEIGHT
        var width = DEFAULT_WIDTH
        var onKeyListener: DialogInterface.OnKeyListener? = null
        var onDismiss: ((Unit) -> Unit)? = null
        var onCancelListener: DialogInterface.OnCancelListener? = null
        var cancelable = true

        @StyleRes
        var animationStyle = 0

        @LayoutRes
        var layoutRes: Int = 0
    }

    private class DialogHolder(private val view: View, private val dialog: MDialog) : View.OnClickListener {
        private var onClick: ((View, MDialog) -> Unit)? = null

        fun addClickIds(clickIds: IntArray) = clickIds.forEach { view.findViewById<View>(it).setOnClickListener(this) }

        override fun onClick(v: View) {
            onClick?.invoke(v, dialog)
        }

        fun setOnViewClick(onClick: ((View, MDialog) -> Unit)?) {
            this.onClick = onClick
        }
    }
}
