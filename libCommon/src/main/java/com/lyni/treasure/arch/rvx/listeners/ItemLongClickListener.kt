package com.lyni.treasure.arch.rvx.listeners

import com.lyni.treasure.arch.rvx.vh.BaseViewHolder

/**
 * @author Liangyong Ni
 * @date 2022/7/11
 * description [ItemLongClickListener]
 */
fun interface ItemLongClickListener<ITEM, VH : BaseViewHolder> {
    /**
     * 长按时执行
     *
     * @param holder    [VH]
     * @param item      [ITEM]
     * @param position  长按的位置
     */
    fun onItemLongClick(holder: VH, item: ITEM, position: Int)
}