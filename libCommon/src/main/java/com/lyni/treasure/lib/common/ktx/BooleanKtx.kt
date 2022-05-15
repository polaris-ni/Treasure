@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description Boolean类的扩展方法
 */
sealed class BooleanExt<out T> constructor(val boolean: Boolean)
object Otherwise : BooleanExt<Nothing>(true)
class WithData<out T>(val data: T) : BooleanExt<T>(false)

inline fun <T> Boolean.positive(block: () -> T): BooleanExt<T> = when {
    this -> {
        WithData(block())
    }
    else -> Otherwise
}

inline fun <T> Boolean.negative(block: () -> T) = when {
    this -> Otherwise
    else -> {
        WithData(block())
    }
}

inline infix fun <T> BooleanExt<T>.otherwise(block: () -> T): T {
    return when (this) {
        is Otherwise -> block()
        is WithData<T> -> this.data
    }
}

inline operator fun <T> Boolean.invoke(block: () -> T) = positive(block)
