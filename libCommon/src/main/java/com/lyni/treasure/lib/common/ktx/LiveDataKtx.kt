@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @date 2022/4/2
 * @author Liangyong Ni
 * description LiveDataKtx
 */
/**
 * 返回一个LiveData，用来暴露给外部
 * @return LiveData
 */
fun <T> MutableLiveData<T>.toLive(): LiveData<T> = this