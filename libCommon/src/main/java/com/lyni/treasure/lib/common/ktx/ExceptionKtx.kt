package com.lyni.treasure.lib.common.ktx

import com.lyni.treasure.lib.common.BuildConfig

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description ExceptionKtx
 */
fun Throwable.printOnDebug() {
    if (BuildConfig.DEBUG) {
        printStackTrace()
    }
}