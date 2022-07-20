@file:Suppress("unused")

package com.lyni.treasure.ktx

import com.lyni.treasure.utils.Log
import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine callback
 *
 * @property dispatcher [CoroutineDispatcher]
 * @property block      coroutine body
 * @property onError    handle exception
 * @constructor Create empty Coroutine callback
 */
data class CoroutineCallback(
    var dispatcher: CoroutineDispatcher? = null,
    var block: suspend () -> Unit = {},
    var onError: (Throwable) -> Unit = { it.printStackTrace() }
)

/**
 * 带异常捕捉的[CoroutineScope.launch]
 *
 * @param init  [CoroutineCallback]
 * @return  [Job]
 */
fun CoroutineScope.launchCatching(init: CoroutineCallback.() -> Unit): Job {
    val callback = CoroutineCallback(dispatcher = Dispatchers.Main).apply { this.init() }
    return launch(CoroutineExceptionHandler { _, throwable ->
        callback.onError(throwable)
    } + (callback.dispatcher ?: Dispatchers.Main)) {
        callback.block()
    }
}

/**
 * Safe launch
 *
 * @param coroutineDispatcher   [CoroutineDispatcher]
 * @param action                [CoroutineCallback.block]
 * @return  [Job]
 */
fun CoroutineScope.safeLaunch(coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main, action: suspend () -> Unit) =
    launchCatching {
        dispatcher = coroutineDispatcher
        block = action
    }

/**
 * [CoroutineScope] with [Dispatchers.Main]
 */
val mainScope: CoroutineScope = CoroutineScope(
    SupervisorJob()
            + Dispatchers.Main.immediate
            + DefaultCoroutineExceptionHandler
)

/**
 * [CoroutineScope] with [Dispatchers.IO]
 */
val ioScope: CoroutineScope = CoroutineScope(
    SupervisorJob()
            + Dispatchers.IO
            + DefaultCoroutineExceptionHandler
)

/**
 * [CoroutineScope] with [Dispatchers.Default]
 */
val calScope: CoroutineScope = CoroutineScope(
    SupervisorJob()
            + Dispatchers.Default
            + DefaultCoroutineExceptionHandler
)

/**
 * 具有异常处理机制并在IO线程执行的协程启动器，适用于网络访问等操作
 * @param action 执行的协程体
 * @return [Job] - Job
 */
fun ioLaunch(action: suspend (CoroutineScope) -> Unit) =
    ioScope.launch {
        action.invoke(this)
    }

/**
 * 具有异常处理机制并在主线程执行的协程启动器，适用于更新UI等操作
 * @param action 执行的协程体
 * @return [Job] - Job
 */
fun mainLaunch(action: suspend (CoroutineScope) -> Unit) =
    mainScope.launch {
        action.invoke(this)
    }

/**
 * 具有异常处理机制的协程启动器，适用于计算密集型任务，如list排序、JSON解析等
 * @param action 执行的协程体
 * @return [Job] - Job
 */
fun calLaunch(action: suspend (CoroutineScope) -> Unit) =
    calScope.launch {
        action.invoke(this)
    }

/**
 * 设置协程异常策略的上下文元素
 */
internal object DefaultCoroutineExceptionHandler
    : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (exception !is CancellationException) {//如果是SupervisorJob就不会传播取消异常，而Job会传播
            Log.e("CoroutineExceptionHandler", "handleException: ", exception)
        }
    }
}