@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.lyni.treasure.utils

import android.annotation.SuppressLint
import com.lyni.treasure.ktx.nowTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description 时间工具类
 */
object DateUtil {
    const val FORMAT_TIME_DAY = "yyyy-MM-dd"
    const val FORMAT_TIME_MINUTE = "yyyy-MM-dd HH:mm"
    const val FORMAT_TIME_SECOND = "yyyy-MM-dd HH:mm:ss"

    @SuppressLint("ConstantLocale")
    private val locale: Locale = Locale.getDefault()

    fun getLocale() = locale

    @JvmStatic
    fun getTimeWhitStamp(timeStamp: Long, format: String = FORMAT_TIME_SECOND): String {
        val sdf = SimpleDateFormat(format, locale)
        return sdf.format(timeStamp)
    }

    fun dayFormat(timeStamp: Long) = getTimeWhitStamp(timeStamp, FORMAT_TIME_DAY)

    fun minuteFormat(timeStamp: Long) = getTimeWhitStamp(timeStamp, FORMAT_TIME_MINUTE)

    fun secondFormat(timeStamp: Long) = getTimeWhitStamp(timeStamp, FORMAT_TIME_SECOND)

    fun getTimeStamp(year: Int, monthOfYear: Int, dayOfMonth: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth, hour, minute)
        return calendar.timeInMillis
    }

    fun getSecond(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        return calendar.get(Calendar.SECOND)
    }

    fun getMinute(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        return calendar.get(Calendar.MINUTE)
    }

    fun getHour(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getDay(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        return calendar.get(Calendar.MONTH)
    }

    fun getRealMonth(timeStamp: Long) = getMonth(timeStamp) + 1

    fun getYear(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        return calendar.get(Calendar.YEAR)
    }

    fun isToday(date: Long, compareTime: Long = nowTime()): Boolean {
        val serverCalendar = Calendar.getInstance()
        serverCalendar.timeInMillis = compareTime
        val purposeCalendar = Calendar.getInstance()
        purposeCalendar.timeInMillis = date
        return serverCalendar.get(Calendar.YEAR) == purposeCalendar.get(Calendar.YEAR) &&
                serverCalendar.get(Calendar.DAY_OF_YEAR) == purposeCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(date: Long, compareTime: Long = nowTime()): Boolean {
        val instance = Calendar.getInstance()
        instance.timeInMillis = date
        val d1 = instance[Calendar.DAY_OF_YEAR]
        instance.timeInMillis = compareTime
        val d2 = instance[Calendar.DAY_OF_YEAR]
        return d2 - d1 == 1
    }

    fun isTomorrow(date: Long, compareTime: Long = nowTime()): Boolean {
        val instance = Calendar.getInstance()
        instance.timeInMillis = date
        val d1 = instance[Calendar.DAY_OF_YEAR]
        instance.timeInMillis = compareTime
        val d2 = instance[Calendar.DAY_OF_YEAR]
        return d1 - d2 == 1
    }

    /**
     * @param scope     scope
     * @param total     剩余时间(毫秒)
     * @param interval  更新间隔(毫秒)
     * @param onTick    更新回调
     * @param onFinish  正常完成时回调
     * @return job
     */
    fun countDownCancellable(
        total: Long, scope: CoroutineScope, interval: Long = 1000,
        onTick: ((Long) -> Unit)? = null, onFinish: (() -> Unit)?
    ) = flow {
        for (i in total downTo 0 step interval) {
            emit(i)
            delay(interval)
        }
    }.flowOn(Dispatchers.Main)
        .onCompletion { cause ->
            if (cause == null) {
                onFinish?.invoke()
            }
        }
        .onEach { onTick?.invoke(it) }
        .launchIn(scope)

    /**
     * 根据生日获取周岁
     * @param birthday 生日
     */
    fun getCurrentAge(birthday: Long): Int {
        //当前时间
        val curr = Calendar.getInstance()
        val born = Calendar.getInstance()
        born.time = Date(birthday)
        //年龄 = 当前年 - 出生年
        var age = curr[Calendar.YEAR] - born[Calendar.YEAR]
        if (age <= 0) {
            return 0
        }
        val currMonth = curr[Calendar.MONTH]
        val currDay = curr[Calendar.DAY_OF_MONTH]
        val bornMonth = born[Calendar.MONTH]
        val bornDay = born[Calendar.DAY_OF_MONTH]
        if (currMonth < bornMonth || currMonth == bornMonth && currDay <= bornDay) {
            age--
        }
        return if (age < 0) 0 else age
    }
}
