package com.ware.widget.ui

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener


/**
 * Created by jianfeng.li on 20-8-5.
 */
private const val TAG = "WrapEditText"

class WrapEditText : AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) //don't ignore default def style
//    textView.getLineBounds (int line, Rect bounds) //get the width and height of this line.

    init {
        /**
         * count: 本次增加的字符数，如果删除则为0
         * start: 本次录入第一个字符位置（0为开始）
         * text: 目前所有字符
         */
        addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
            Log.d(TAG, "text = $text; start = $start;  before = $before; count = $count")
            Log.d(TAG, "lineCount: $lineCount; maxLines = $maxLines")
            val layout: Layout = layout //the Layout that is currently being used to display the text.

            //for maxLines implement
            if (maxLines in 1 until lineCount && !text.isNullOrEmpty()) {
                val sb = StringBuilder()
                var lineStart = 0
                var lineEnd: Int
                for (i in 0 until maxLines) {
                    lineEnd = layout.getLineEnd(i) //get the text on a given line,   layout.getLineStart()
                    sb.append(text.substring(lineStart, lineEnd))
                    lineStart = lineEnd
                }
                setText(sb.toString())
                setSelection(sb.length) //set cursor position in EditText
            }
        })
    }
}