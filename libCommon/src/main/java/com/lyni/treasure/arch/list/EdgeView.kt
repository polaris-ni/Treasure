package com.lyni.treasure.arch.list

import android.view.View
import android.view.ViewGroup

/**
 * @author Liangyong Ni
 * @date 2022/7/4
 * description [EdgeView] 表头与表尾
 * @see [BaseRVAdapter]
 */
fun interface EdgeView {
    fun initView(parent: ViewGroup, viewType: Int): View
}