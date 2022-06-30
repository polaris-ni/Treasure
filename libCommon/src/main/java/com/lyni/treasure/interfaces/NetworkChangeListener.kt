package com.lyni.treasure.interfaces

import com.lyni.treasure.arch.network.NetworkType

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description [NetworkChangeListener]
 */
interface NetworkChangeListener {

    /**
     * [onChanged] will be called when network changed or listener is registered firstly
     *
     * @param current   [NetworkType] - current network type
     * @param last      [NetworkType] - last network type
     */
    fun onChanged(current: NetworkType, last: NetworkType)
}