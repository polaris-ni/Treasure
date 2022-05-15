@file:Suppress("unused")

package com.lyni.treasure.lib.common.utils

import android.widget.Toast

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description toast组件
 */
object ToastUtil {
    fun showToast(msg: String?) {
        Toast.makeText(Utils.getAppContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(msg: String?) {
        Toast.makeText(Utils.getAppContext(), msg, Toast.LENGTH_LONG).show()
    }
}