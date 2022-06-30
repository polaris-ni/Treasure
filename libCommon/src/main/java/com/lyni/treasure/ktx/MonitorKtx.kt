@file:Suppress("unused")

package com.lyni.treasure.ktx

import android.os.SystemClock
import com.lyni.treasure.utils.Log

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 性能监控
 */
/**
 * 测量方法执行时间并打印
 * @param tag TAG
 * @param monitorEvent 执行时间回调
 * @param action 方法体
 * @return 执行结果
 */
inline fun <T> measureTime(
    tag: String = "Treasure Measure Time",
    monitorEvent: (Long) -> Unit = {
        Log.d(tag, "watchFunctionTime: $it")
    },
    action: () -> T
): T {
    val start = SystemClock.elapsedRealtime()
    val value = action.invoke()
    monitorEvent.invoke(SystemClock.elapsedRealtime() - start)
    return value
}