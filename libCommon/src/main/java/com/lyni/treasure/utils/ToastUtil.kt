@file:Suppress("unused")

package com.lyni.treasure.utils

import android.widget.Toast
import com.lyni.treasure.ktx.runOnUiThread

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description toast组件
 */
object ToastUtil {
    /**
     * 显示一个Toast
     * @param msg 内容
     * @param isLong 是否显示较长时间
     */
    fun showToast(msg: String?, isLong: Boolean = false) {
        runOnUiThread {
            Toast.makeText(Utils.getAppContext(), msg, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
        }
    }
}

fun showToast(msg: String?) = ToastUtil.showToast(msg)

fun showLongToast(msg: String?) = ToastUtil.showToast(msg, true)