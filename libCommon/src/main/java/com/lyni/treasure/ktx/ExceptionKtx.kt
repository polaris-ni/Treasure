package com.lyni.treasure.ktx

import com.lyni.treasure.utils.Utils

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description ExceptionKtx
 */
fun Throwable.printOnDebug() {
    if (Utils.isDebug()) {
        printStackTrace()
    }
}