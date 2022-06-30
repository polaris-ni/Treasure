package com.lyni.treasure.ktx

import android.os.Handler
import android.os.Looper

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description ThreadKtx
 */
/**
 * This main looper cache avoids synchronization overhead when accessed repeatedly.
 */
@JvmField
val mainLooper: Looper = Looper.getMainLooper()

/**
 * Main Thread
 */
@JvmField
val mainThread: Thread = mainLooper.thread

@JvmField
val mainHandler: Handler = Handler(Looper.getMainLooper())

/**
 * 判断是否是主线程
 */
fun isMainThread() = mainThread === Thread.currentThread()

fun runOnUiThread(runnable: Runnable) {
    if (isMainThread()) {
        runnable.run()
    } else {
        mainHandler.post(runnable)
    }
}

fun runOnUiThreadDelayed(duration: Long, runnable: Runnable) {
    mainHandler.postDelayed(runnable, duration)
}

