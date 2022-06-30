@file:Suppress("unused")

package com.lyni.treasure.ktx

import androidx.annotation.IntRange

/**
 * @date 2022/4/16
 * @author Liangyong Ni
 * description ListKtx
 */
/**
 * 返回可变列表的子列表，如果范围超出则取重合的一部分，否则返回空列表
 * @param from 子列表开始索引，需要大于0
 * @param to 子列表结束索引，需要大于0
 * @return 子列表
 */
fun <T> MutableList<T>.subListOrEmpty(@IntRange(from = 0L) from: Int, @IntRange(from = 0L) to: Int): MutableList<T> {
    return if (from >= to || from >= size) {
        mutableListOf()
    } else {
        if (to <= size) subList(from, to) else subList(from, size)
    }
}