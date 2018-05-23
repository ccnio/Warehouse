package com.edreamoon.warehouse.img

import android.content.Context
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.view.View
import com.edreamoon.warehouse.R

class MDrawableStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var currentState = NONE
    override fun onCreateDrawableState(extraSpace: Int): IntArray {

        if (currentState != NONE) {
            val stateSets = super.onCreateDrawableState(extraSpace + 1)
            when (currentState) {
                ONE -> mergeDrawableStates(stateSets, STATE_NORMAL)
                TWO -> mergeDrawableStates(stateSets, STATE_PRESSED)
            }
            return stateSets
        }
        return super.onCreateDrawableState(extraSpace)
    }

    @IntDef(ONE, TWO)
    annotation class State

    companion object {
        const val ONE = 1
        const val TWO = 2
        const val NONE = 0

        val STATE_NORMAL = intArrayOf(R.attr.normal)
        val STATE_PRESSED = intArrayOf(R.attr.pressed)
    }

    fun setState(@State state: Int) {
        currentState = state
        refreshDrawableState()
    }
}