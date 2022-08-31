package com.ccnio.mdialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment

/**
 * Created by jianfeng.li on 2021/6/13.
 */
private const val KEY_SAVED_INSTANCE = "dialog_instance"
private const val KEY_SAVED_INSTANCE2 = "dialog_"
private const val TAG = "MDialog"

/**
 * # 通过建造者模式来构造Dialog时，需要外部禁止直接通过构造函数创建，所以 constructor 需要加 protected,这样也不会影响继承使用
 * # View,回调本身不可序列化，但在 onSaveInstanceState 居然可以恢复(serializable,parcelable都可以)，原因可能是非跨进程操作。跨进程操作就不允许这样操作
 */
abstract class BaseDialogFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//必须写在这里，不然宽度不对
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(getGravity())
            attributes.x = getOffsetX()
            attributes.y = getOffsetY()
            attributes = attributes
            setWindowAnimations(getAnimation())
        }
        return getDialogView()?.apply { (parent as? ViewGroup)?.removeView(this) }
            ?: inflater.inflate(getLayoutRes(), container).apply { bindView(this) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            setCanceledOnTouchOutside(isCanceledOnTouchOutside())
        }
        dialog?.window?.run {
            setLayout(getWidth(), getHeight())
        }
    }

    protected abstract fun bindView(view: View)
    protected open fun getWidth() = ViewGroup.LayoutParams.WRAP_CONTENT
    protected open fun getHeight() = ViewGroup.LayoutParams.WRAP_CONTENT
    @StyleRes protected open fun getAnimation() = 0
    protected open fun getGravity() = Gravity.CENTER
    protected open fun getOffsetX() = 0
    protected open fun getOffsetY() = 0
    protected open fun getDialogView(): View? = null
    @LayoutRes protected abstract fun getLayoutRes(): Int
    protected open fun isCanceledOnTouchOutside() = false
}