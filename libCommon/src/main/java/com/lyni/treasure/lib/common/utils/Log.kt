@file:Suppress("unused")

package com.lyni.treasure.lib.common.utils

import android.util.Log

/**
 * @date 2022/5/13
 * @author Liangyong Ni
 * description Log 非Debug不打印
 */
object Log {
    private var isDebug = false

    fun openDebug() {
        isDebug = true
    }

    fun isDebug() = isDebug

    fun e(tag: String, msg: String) {
        if (isDebug) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String, e: Throwable) {
        if (isDebug) {
            Log.e(tag, msg, e)
        }
    }

    fun w(tag: String, msg: String) {
        if (isDebug) {
            Log.w(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (isDebug) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (isDebug) {
            Log.i(tag, msg)
        }
    }
}