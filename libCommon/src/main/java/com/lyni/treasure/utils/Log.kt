@file:Suppress("unused")

package com.lyni.treasure.utils

import android.util.Log

/**
 * @date 2022/5/13
 * @author Liangyong Ni
 * description Log 非Debug不打印
 */
object Log {

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        if (Utils.isDebug()) {
            Log.e(tag, msg, tr)
        }
    }

    fun w(tag: String, msg: String, tr: Throwable? = null) {
        if (Utils.isDebug()) {
            Log.w(tag, msg, tr)
        }
    }

    fun d(tag: String, msg: String, tr: Throwable? = null) {
        if (Utils.isDebug()) {
            Log.d(tag, msg, tr)
        }
    }

    fun i(tag: String, msg: String, tr: Throwable? = null) {
        if (Utils.isDebug()) {
            Log.i(tag, msg, tr)
        }
    }

    fun v(tag: String, msg: String?, tr: Throwable? = null) {
        if (Utils.isDebug()) {
            Log.v(tag, msg, tr)
        }
    }
}