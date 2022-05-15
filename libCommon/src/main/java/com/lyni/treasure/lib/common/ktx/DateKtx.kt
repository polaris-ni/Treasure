@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import com.lyni.treasure.lib.common.utils.DateUtil
import java.util.*

/**
 * @date 2022/5/16
 * @author Liangyong Ni
 * description DateKtx
 */
fun Date.secondFormat() = DateUtil.secondFormat(this.time)

fun Date.minuteFormat() = DateUtil.minuteFormat(this.time)

fun Date.dayFormat() = DateUtil.dayFormat(this.time)