package com.lyni.treasure.arch.list.listeners

import com.lyni.treasure.arch.list.vh.BaseViewHolder

/**
 * @author Liangyong Ni
 * @date 2022/7/11
 * description [ItemClickListener]
 */
fun interface ItemClickListener<ITEM, VH : BaseViewHolder> {
    /**
     * 点击时执行
     *
     * @param holder    [VH]
     * @param item      [ITEM]
     * @param position  点击的位置
     */
    fun onItemClick(holder: VH, item: ITEM, position: Int)
}