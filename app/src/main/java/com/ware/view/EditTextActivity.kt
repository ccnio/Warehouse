package com.ware.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ware.R


/**
 * 1.itText自动换行，竖直方向可滚动 处理嵌套滑动
 */
class EditTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_text)

//        mEditView.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(view: View, event: MotionEvent): Boolean {
////                if (view.getId() === R.id.DwEdit) {
//                view.getParent().requestDisallowInterceptTouchEvent(true)
//                when (event.getAction() and MotionEvent.ACTION_MASK) {
//                    MotionEvent.ACTION_UP -> view.getParent().requestDisallowInterceptTouchEvent(false)
//                }
////                }
//                return false
//            }
//
//        })

    }
}
