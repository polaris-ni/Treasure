package com.lyni.treasure.utils

import android.content.Context

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 工具类
 */
object Utils {
    private lateinit var applicationContext: Context

    private var isDebug = false

    fun isDebug(): Boolean = isDebug

    fun init(context: Context) {
        applicationContext = context
    }

    fun getAppContext() = applicationContext

    /**
     * 打开日志的Debug模式，默认关闭
     */
    fun openDebug() {
        isDebug = true
    }
}