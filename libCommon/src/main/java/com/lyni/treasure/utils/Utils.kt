package com.lyni.treasure.utils

import android.content.Context

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 工具类
 */
object Utils {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }

    fun getAppContext() = applicationContext
}