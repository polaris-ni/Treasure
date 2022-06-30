@file:Suppress("unused")

package com.lyni.treasure.utils

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

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        if (isDebug) {
            Log.e(tag, msg, tr)
        }
    }

    fun w(tag: String, msg: String, tr: Throwable? = null) {
        if (isDebug) {
            Log.w(tag, msg, tr)
        }
    }

    fun d(tag: String, msg: String, tr: Throwable? = null) {
        if (isDebug) {
            Log.d(tag, msg, tr)
        }
    }

    fun i(tag: String, msg: String, tr: Throwable? = null) {
        if (isDebug) {
            Log.i(tag, msg, tr)
        }
    }

    fun v(tag: String, msg: String?, tr: Throwable? = null) {
        if (isDebug) {
            Log.v(tag, msg, tr)
        }
    }
}