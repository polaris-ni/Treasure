@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import com.lyni.treasure.lib.common.utils.Log
import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * 具有异常处理机制并在主线程执行的协程启动器，适用于更新UI等操作
 * @param action 执行的协程体
 * @return Job
 */
fun CoroutineScope.safeLaunch(action: suspend () -> Unit = {}): Job {
    return launch(CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() } + Dispatchers.Main) {
        action.invoke()
    }
}

val mainScope: CoroutineScope = CoroutineScope(
    SupervisorJob()
            + Dispatchers.Main.immediate
            + CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow
)

val ioScope: CoroutineScope = CoroutineScope(
    SupervisorJob()
            + Dispatchers.IO
            + CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow
)

/**
 * 具有异常处理机制并在IO线程执行的协程启动器，适用于网络访问等操作
 * @param action 执行的协程体
 * @return Job
 */
fun ioLaunch(action: suspend (CoroutineScope) -> Unit) =
    ioScope.launch {
        action.invoke(this)
    }

/**
 * 具有异常处理机制并在主线程执行的协程启动器，适用于更新UI等操作
 * @param action 执行的协程体
 * @return Job
 */
fun mainLaunch(action: suspend (CoroutineScope) -> Unit) =
    mainScope.launch {
        action.invoke(this)
    }

/**
 * 设置协程异常策略的上下文元素
 */
object CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow
    : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (exception !is CancellationException) {//如果是SupervisorJob就不会传播取消异常，而Job会传播
            Log.e("CoroutineExceptionHandler", "handleException: ", exception)
        }
    }
}

/**
 * 具有延迟执行和重复执行功能并在主线程上运行的协程启动器
 * @param tag 日志TAG
 * @param delay 延迟执行时间，默认不延迟
 * @param repeat 重复次数，默认执行一次
 * @param duration 每次执行时间隔时间，以一次任务执行完算起，默认无间隔
 * @param action 执行的协程体
 * @return Job
 */
fun timedLaunch(
    tag: String = "Timed Task",
    delay: Long = 0L,
    repeat: Long = 1,
    duration: Long = 0,
    action: suspend (CoroutineScope) -> Unit
) = mainLaunch {
    check(delay >= 0) { "timeMills must be positive" }
    delay(delay)
    var i = 0
    while (i < repeat) {
        Log.i(tag, "Repeat $i in $repeat")
        action.invoke(it)
        delay(duration)
        i += 1
    }
}