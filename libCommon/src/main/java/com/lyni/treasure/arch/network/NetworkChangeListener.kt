package com.lyni.treasure.arch.network

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description [NetworkChangeListener]只有当网络状态发生变化才会回调[onChanged]
 */
interface NetworkChangeListener : NetworkStatusListener {

    override fun onReceiveStatus(current: NetworkType, last: NetworkType) {
        if (current == last) {
            return
        }
        onChanged(current)
    }

    fun onChanged(type: NetworkType)
}