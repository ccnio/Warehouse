package com.ccnio.ware.view.statelayout

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R

class StateLayoutActivity : AppCompatActivity() {
    private lateinit var stateLayout: PageStateLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testXml()
//        testCode()
    }

    private fun testCode() {
        setContentView(R.layout.activity_state_layout2)
        val stateLayout = PageStateLayout(this).apply {
            inject(this@StateLayoutActivity.findViewById<View>(R.id.constrain_view))
//            setGravity(Gravity.START, xOffset = -100, yOffset = -200)
        }
        stateLayout.showLoading("loading...")
//        stateLayout.showError()
        stateLayout.postDelayed({
            stateLayout.showError("error") {
                stateLayout.setGravity(Gravity.START or Gravity.CENTER_HORIZONTAL, 20)
                stateLayout.showLoading("loading+1")
                stateLayout.postDelayed({ stateLayout.showEmpty() }, 4000)

            }
        }, 2000)
    }

    private fun testXml() {
        setContentView(R.layout.activity_state_layout)
        stateLayout = findViewById(R.id.constrain_view)

        stateLayout.showLoading()
        stateLayout.postDelayed({
            stateLayout.showNetError {
                stateLayout.showLoading("loading+1")
                stateLayout.postDelayed({ stateLayout.showContent() }, 2000)

            }
        }, 2000)
    }
}