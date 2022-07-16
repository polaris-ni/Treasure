package com.lyni.treasure.arch.network

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description [NetworkStatusListener]当接收到网络连接广播就会回调
 */
fun interface NetworkStatusListener {

    /**
     * [onReceiveStatus] will be called when network changed or listener is registered firstly
     *
     * @param current   [NetworkType] - current network type
     * @param last      [NetworkType] - last network type
     */
    fun onReceiveStatus(current: NetworkType, last: NetworkType)
}