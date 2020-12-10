package com.ware.component.inflater

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.ware.R

/**
 * Created by jianfeng.li on 20-12-3.
 *
 * activity should be instance of AppCompatActivity
 */
private const val TAG = "InflaterActivity"

class LayoutFactory(private val inflaterActivity: InflaterActivity) : LayoutInflater.Factory2 {
    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
//        context as AppCompatActivity
        val delegate: AppCompatDelegate = inflaterActivity.delegate
        val view: View? = delegate.createView(parent, name, context, attrs)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayoutExtAttrs)
        val n = attrs.attributeCount //view的属性
        for (i in 0 until n) Log.e(TAG, attrs.getAttributeName(i).toString() + " , " + attrs.getAttributeValue(i))
////        typedArray.getColor()
        val str = typedArray.getTextArray(R.styleable.LayoutExtAttrs_deviceTypeStr)
        if (str != null)
            Log.d(TAG, "onCreateView: str = ${str[0]}; ${str[1]}")
        typedArray.recycle()

        Log.d(TAG, "onCreateView: name = $name; view = $view")
//        if(view is TextView){
//            view.text = str
//        }
        return view
//        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Log.d(TAG, "onCreateView: ")
        return null
    }
}