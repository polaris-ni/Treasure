package com.lyni.treasure.components

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.lyni.treasure.interfaces.ProactiveLifecycleEventObserver
import com.lyni.treasure.utils.Log

/**
 * @date 2022/5/24
 * @author Liangyong Ni
 * description [KeyboardChangeListener] - 键盘高度变化监听器
 */
open class KeyboardChangeListener(
    private var activity: FragmentActivity? = null,
    private val onChange: (keyboardHeight: Int) -> Unit
) : ViewTreeObserver.OnGlobalLayoutListener, ProactiveLifecycleEventObserver {
    companion object {
        private const val TAG = "KeyboardChangeListener"
    }

    private var mWindowHeight = 0

    override fun onGlobalLayout() {
        val r = Rect()
        //获取当前窗口实际的可见区域
        requireActivity().window.decorView.getWindowVisibleDisplayFrame(r)
        val height = r.height()
        if (mWindowHeight == 0) {
            //一般情况下，这是原始的窗口高度
            mWindowHeight = height
        } else {
            //两次窗口高度相减，就是软键盘高度
            val softKeyboardHeight = mWindowHeight - height
            onChange.invoke(softKeyboardHeight)
        }
    }

    protected open fun addListener() {
        requireActivity().run {
            window.decorView.viewTreeObserver.addOnGlobalLayoutListener(this@KeyboardChangeListener)
            lifecycle.addObserver(this@KeyboardChangeListener)
        }
        Log.d(TAG, "GlobalLayoutChangeListener is added")
    }

    protected open fun removeListener() {
        requireActivity().window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        activity = null
        Log.d(TAG, "GlobalLayoutChangeListener is removed")
    }

    private fun requireActivity(): FragmentActivity {
        if (activity == null) {
            throw RuntimeException("Activity is not attached to KeyboardStateChangeListener")
        }
        return activity!!
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> addListener()
            Lifecycle.Event.ON_DESTROY -> removeListener()
            else -> {}
        }
    }
}