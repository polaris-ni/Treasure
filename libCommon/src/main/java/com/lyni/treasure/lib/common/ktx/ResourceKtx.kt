@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import androidx.core.content.ContextCompat
import com.lyni.treasure.lib.common.utils.Utils

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 资源类扩展方法
 */
val Int.getString
    get() = Utils.getAppContext().getString(this)

val Int.getDrawable
    get() = ContextCompat.getDrawable(Utils.getAppContext(), this)?.apply {
        setBounds(0, 0, minimumWidth, minimumHeight)
    }

val Int.getColor
    get() = ContextCompat.getColor(Utils.getAppContext(), this)