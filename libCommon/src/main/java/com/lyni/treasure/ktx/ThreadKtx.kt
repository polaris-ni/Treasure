package com.lyni.treasure.ktx

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

/**
 * 判断是否是主线程
 */
fun isMainThread() = mainThread === Thread.currentThread()

