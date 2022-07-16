@file:Suppress("unused")

package com.lyni.treasure.utils

import com.lyni.treasure.ktx.mainLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description 用于执行定时任务的工具类
 */
/**
 * 从[total]开始，每隔[interval]执行一次[onTick]函数，正常结束后回调[onFinish]
 *
 * @param scope     scope
 * @param total     总时间(毫秒)
 * @param interval  更新间隔(毫秒)
 * @param onTick    更新回调
 * @param onFinish  完成时回调，发生异常传入的参数不为空
 * @return [Job] - 用来控制任务
 */
fun countDown(
    total: Long, scope: CoroutineScope, interval: Long = 1000,
    onTick: ((Long) -> Unit)? = null, onFinish: ((Throwable?) -> Unit)?
): Job = flow {
    for (i in total downTo 0 step interval) {
        emit(i)
        delay(interval)
    }
}.flowOn(Dispatchers.Main)
    .onCompletion { cause ->
        onFinish?.invoke(cause)
    }
    .onEach { onTick?.invoke(it) }
    .launchIn(scope)

/**
 * 具有延迟执行和重复执行功能并在主线程上运行的协程启动器
 * 从函数调用开始，延迟[delay]后执行一次[action]，此后每隔[duration]重复执行一次[action]，总共执行[repeat]次
 *
 * @param tag       日志TAG，null表示关闭日志打印
 * @param delay     延迟执行时间，默认不延迟
 * @param repeat    重复次数，默认执行一次
 * @param duration  每次执行时间隔时间，以一次任务执行完算起，默认无间隔
 * @param action    执行的协程体
 * @return [Job] - 用来控制任务
 */
fun timedLaunch(
    tag: String? = "Timed Task",
    delay: Long = 0L,
    repeat: Long = 1,
    duration: Long = 0,
    action: suspend (CoroutineScope) -> Unit
) = mainLaunch {
    check(delay >= 0) { "timeMills must be positive" }
    delay(delay)
    var i = 0
    while (i < repeat) {
        if (tag != null) {
            Log.i(tag, "Repeat $i in $repeat")
        }
        action.invoke(it)
        delay(duration)
        i += 1
    }
}