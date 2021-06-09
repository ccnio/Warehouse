package com.ware.jetpack.binding

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by jianfeng.li on 2021/5/28.
 */
class FragmentViewBindingProperty<in F : Fragment, out T : ViewBinding>(val viewBinder: (F) -> T) : ReadOnlyProperty<F, T> {

    private var viewBinding: T? = null

    @MainThread
    override fun getValue(thisRef: F, property: KProperty<*>): T {
        viewBinding?.let { return it }

        val lifecycle = thisRef.lifecycle
        val viewBinding = viewBinder(thisRef)
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            // We can access to ViewBinding after Fragment.onDestroyView(), but don't save it to prevent memory leak
        } else {
            lifecycle.addObserver(BindingLifecycleObserver())
            this.viewBinding = viewBinding
        }
        return viewBinding
    }

    private inner class BindingLifecycleObserver : DefaultLifecycleObserver {
        private val mainHandler = Handler(Looper.getMainLooper())

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            // Fragment.viewLifecycleOwner call LifecycleObserver.onDestroy() before Fragment.onDestroyView().
            // That's why we need to postpone reset of the viewBinding
            mainHandler.post {
                viewBinding = null
            }
        }
    }
}