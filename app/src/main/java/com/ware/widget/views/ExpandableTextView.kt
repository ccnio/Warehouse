package com.ware.widget.views

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.ware.R

/**
 * Created by jianfeng.li on 2021/6/9.
 */
private const val TAG = "ExpandableTextView2"

class ExpandableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatTextView(context, attrs, defStyle) {
    private val moreText = "更多"
    private val lessText = "收起"
    private val ellipsizeText: String
    private var oriCharSequence: String? = null


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            oriCharSequence = s?.toString()
            showLess()
        }
    }

    private val lessLines: Int
    private val expandTextColor: Int
    private val expandTextSize: Float

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyle, 0)
        lessLines = a.getInt(R.styleable.ExpandableTextView_expandLines, 2)
        expandTextColor = a.getColor(R.styleable.ExpandableTextView_expandTextColor, Color.RED)
        expandTextSize = a.getDimension(R.styleable.ExpandableTextView_expandTextSize, textSize)
        ellipsizeText = a.getString(R.styleable.ExpandableTextView_expandEllipsize)
            ?: String(charArrayOf('\u2026', ' '))
        a.recycle()

        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
        addTextChangedListener(textWatcher)
    }

    fun showLess() {
        if (lessLines >= lineCount || layout == null) return

        oriCharSequence?.let {
            val lineEndIndex = layout.getLineEnd(lessLines - 1)
            Log.d(TAG, "showLess: endIndex = $lineEndIndex; str = ${it.subSequence(0, lineEndIndex)}")
            val showText = "${it.subSequence(0, lineEndIndex - ellipsizeText.length - moreText.length)}$ellipsizeText$moreText"
            Log.d(TAG, "showLess: lineEndIndex = $lineEndIndex; expandLen = ${moreText.length}; res = $showText")
            setTextSpan(genClickSpan(showText, moreText, true))
        }
    }

    fun showMore() {
        if (layout == null) return

        oriCharSequence?.let {
            val retString = "$it$lessText"
            Log.d(TAG, "showMore:  res = $retString")
            setTextSpan(genClickSpan(retString, lessText, false))
        }
    }

    private fun setTextSpan(span: SpannableStringBuilder) {
        removeTextChangedListener(textWatcher)
        text = span
        addTextChangedListener(textWatcher)
    }

    private fun genClickSpan(text: String, spanText: String, more: Boolean): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(text)
        val contains = text.contains(spanText)
        Log.d(TAG, "genClickSpan: $contains")
//        if (contains) {
        val lastIndexOf = text.lastIndexOf(spanText)
        ssb.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = expandTextColor
                ds.isUnderlineText = false
            }

            override fun onClick(widget: View) {
                if (more) showMore()
                else showLess()
            }
        }, lastIndexOf, lastIndexOf + spanText.length, 0) //flags: no useage
//        }
        ssb.setSpan(AbsoluteSizeSpan(expandTextSize.toInt()), lastIndexOf, lastIndexOf + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }
}