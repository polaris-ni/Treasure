@file:Suppress("unused")

package com.lyni.treasure.ktx

import com.lyni.treasure.utils.Log
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