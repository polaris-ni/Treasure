@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import androidx.core.content.ContextCompat
import com.lyni.treasure.lib.common.utils.Utils

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 资源类扩展方法
 */
fun Int.getString() = Utils.getAppContext().getString(this)

fun Int.getDrawable() = ContextCompat.getDrawable(Utils.getAppContext(), this)?.apply {
    setBounds(0, 0, minimumWidth, minimumHeight)
}

fun Int.getColor() = ContextCompat.getColor(Utils.getAppContext(), this)