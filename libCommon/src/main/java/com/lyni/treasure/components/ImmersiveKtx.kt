@file:Suppress("DEPRECATION", "UNUSED")

package com.lyni.treasure.components

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lyni.treasure.common.R


/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description Immersive Config
 */
/**
 * 设置状态栏文字颜色，不会修改背景颜色，使用[setStatusBarColor]修改状态栏颜色
 *
 * @param isLightingColor   false表示文字颜色设置为浅色，true为深色，
 */
fun Activity.setLightStatusBar(isLightingColor: Boolean) {
    val window = this.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (isLightingColor) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}

/**
 * 将导航栏设置为浅色
 *
 * @param isLightingColor   true为浅色
 */
fun Activity.setLightNavigationBar(isLightingColor: Boolean) {
    val window = this.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLightingColor) {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0
    }
}

/**
 * 沉浸式状态栏，必须在[Activity.onCreate]时调用
 */
fun Activity.immersiveStatusBar() {
    val view = (window.decorView as ViewGroup).getChildAt(0)
    view.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
        val lp = view.layoutParams as FrameLayout.LayoutParams
        if (lp.topMargin > 0) {
            lp.topMargin = 0
            v.layoutParams = lp
        }
        if (view.paddingTop > 0) {
            view.setPadding(0, 0, 0, view.paddingBottom)
            val content = findViewById<View>(android.R.id.content)
            content.requestLayout()
        }
    }

    val content = findViewById<View>(android.R.id.content)
    content.setPadding(0, 0, 0, content.paddingBottom)

    window.decorView.findViewById(R.id.status_bar_view) ?: View(window.context).apply {
        id = R.id.status_bar_view
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight())
        params.gravity = Gravity.TOP
        layoutParams = params
        (window.decorView as ViewGroup).addView(this)

        (window.decorView as ViewGroup).setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child?.id == android.R.id.statusBarBackground) {
                    child.scaleX = 0f
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
            }
        })
    }
    setStatusBarColor(Color.TRANSPARENT)
}

/**
 * 沉浸式导航栏，必须在[Activity.onCreate]时调用
 */
fun Activity.immersiveNavigationBar() {
    val view = (window.decorView as ViewGroup).getChildAt(0)
    view.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
        val lp = view.layoutParams as FrameLayout.LayoutParams
        if (lp.bottomMargin > 0) {
            lp.bottomMargin = 0
            v.layoutParams = lp
        }
        if (view.paddingBottom > 0) {
            view.setPadding(0, view.paddingTop, 0, 0)
            val content = findViewById<View>(android.R.id.content)
            content.requestLayout()
        }
    }

    val content = findViewById<View>(android.R.id.content)
    content.setPadding(0, content.paddingTop, 0, -1)

    val heightLiveData = MutableLiveData<Int>()
    heightLiveData.value = 0
    window.decorView.setTag(R.id.navigation_height_live_data, heightLiveData)

    window.decorView.findViewById(R.id.navigation_bar_view) ?: View(window.context).apply {
        id = R.id.navigation_bar_view
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, heightLiveData.value ?: 0)
        params.gravity = Gravity.BOTTOM
        layoutParams = params
        (window.decorView as ViewGroup).addView(this)

        if (this@immersiveNavigationBar is FragmentActivity) {
            heightLiveData.observe(this@immersiveNavigationBar) {
                val lp = layoutParams
                lp.height = heightLiveData.value ?: 0
                layoutParams = lp
            }
        }

        (window.decorView as ViewGroup).setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child?.id == android.R.id.navigationBarBackground) {
                    child.scaleX = 0f
                    bringToFront()

                    child.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
                        heightLiveData.value = bottom - top
                    }
                } else if (child?.id == android.R.id.statusBarBackground) {
                    child.scaleX = 0f
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
            }
        })
    }
    setNavigationBarColor(Color.TRANSPARENT)
}

/**
 * 是否将布局延伸到状态栏
 * @param fit   true表示预留状态栏，false表示将布局延伸到状态栏内
 */
fun Activity.fitStatusBar(fit: Boolean) {
    val content = findViewById<View>(android.R.id.content)
    if (fit) {
        content.setPadding(0, getStatusBarHeight(), 0, content.paddingBottom)
    } else {
        content.setPadding(0, 0, 0, content.paddingBottom)
    }
}

/**
 * 是否将布局延伸到导航栏
 * @param fit   true表示预留导航栏，false表示将布局延伸到导航栏内
 */
fun Activity.fitNavigationBar(fit: Boolean) {
    val content = findViewById<View>(android.R.id.content)
    if (fit) {
        content.setPadding(0, content.paddingTop, 0, getNavigationBarHeightLiveData().value ?: 0)
    } else {
        content.setPadding(0, content.paddingTop, 0, -1)
    }
    if (this is FragmentActivity) {
        getNavigationBarHeightLiveData().observe(this) {
            if (content.paddingBottom != -1) {
                content.setPadding(0, content.paddingTop, 0, it)
            }
        }
    }
}

fun Activity.isImmersiveNavigationBar(): Boolean {
    return window.attributes.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION != 0
}

fun Context.getStatusBarHeight(): Int {
    val resourceId =
        resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId)
    }
    return 0
}

fun Context.getNavigationHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

fun Activity.setStatusBarColor(color: Int) {
    val statusBarView = window.decorView.findViewById<View>(R.id.status_bar_view)
    if (color == 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        statusBarView?.setBackgroundColor(STATUS_BAR_MASK_COLOR)
    } else {
        statusBarView?.setBackgroundColor(color)
    }
}

fun Activity.setNavigationBarColor(color: Int) {
    val navigationBarView = window.decorView.findViewById<View>(R.id.navigation_bar_view)
    if (color == 0 && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        navigationBarView?.setBackgroundColor(STATUS_BAR_MASK_COLOR)
    } else {
        navigationBarView?.setBackgroundColor(color)
    }
}

@Suppress("UNCHECKED_CAST")
fun Activity.getNavigationBarHeightLiveData(): LiveData<Int> {
    var liveData = window.decorView.getTag(R.id.navigation_height_live_data) as? LiveData<Int>
    if (liveData == null) {
        liveData = MutableLiveData()
        window.decorView.setTag(R.id.navigation_height_live_data, liveData)
    }
    return liveData
}

/**
 * 设置状态栏文字颜色，不会修改背景颜色
 *
 * @param isLightingColor   false表示文字颜色设置为浅色，true为深色，
 */
fun Dialog.setLightStatusBar(isLightingColor: Boolean) {
    val window = this.window ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (isLightingColor) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}

fun Dialog.setLightNavigationBar(isLightingColor: Boolean) {
    val window = this.window ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLightingColor) {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0
    }
}

fun Dialog.immersiveStatusBar() {
    val window = this.window ?: return
    (window.decorView as ViewGroup).setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View?, child: View?) {
            if (child?.id == android.R.id.statusBarBackground) {
                child.scaleX = 0f
            }
        }

        override fun onChildViewRemoved(parent: View?, child: View?) {
        }
    })
}

fun Dialog.immersiveNavigationBar() {
    val window = this.window ?: return
    (window.decorView as ViewGroup).setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View?, child: View?) {
            if (child?.id == android.R.id.navigationBarBackground) {
                child.scaleX = 0f
            } else if (child?.id == android.R.id.statusBarBackground) {
                child.scaleX = 0f
            }
        }

        override fun onChildViewRemoved(parent: View?, child: View?) {
        }
    })
}

private const val STATUS_BAR_MASK_COLOR = 0x7F000000