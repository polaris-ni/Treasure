@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import android.content.Context

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 尺寸单位换算相关扩展属性
 */

/**
 * context中 dp <-> px <-> sp
 */
fun Context.dp2px(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Context.dp2pxF(dp: Float): Float {
    val scale = resources.displayMetrics.density
    return dp * scale + 0.5f
}

fun Context.px2dp(px: Float): Int {
    val scale = resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

fun Context.px2sp(px: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (px / scale + 0.5f).toInt()
}

fun Context.sp2px(sp: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (sp * scale + 0.5f).toInt()
}
