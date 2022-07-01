@file:Suppress("unused")

package com.lyni.treasure.ktx

import android.os.Build

/**
 * @date 2022/3/24
 * @author Liangyong Ni
 * description SystemKtx
 */
/**
 * 获取当前时间
 * @return 当前时间（毫秒）
 */
fun nowTime() = System.currentTimeMillis()

inline fun sdkCheck(targetSdkVersion: Int, action: () -> Unit) = runCatching {
    if (Build.VERSION.SDK_INT >= targetSdkVersion) {
        action.invoke()
    } else {
        throw IllegalStateException()
    }
}