package com.lyni.treasure.arch.network

import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.lyni.treasure.interfaces.ProactiveLifecycleEventObserver

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description 继承了[ProactiveLifecycleEventObserver]，通过与[Lifecycle]联合使用实现自动注册与注销
 */
abstract class NetworkChangeLifecycleListener(
    private val startEvent: Lifecycle.Event = Lifecycle.Event.ON_CREATE,
    private val endEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) : NetworkChangeListener, ProactiveLifecycleEventObserver {

    init {
        if (startEvent >= endEvent) {
            throw IllegalArgumentException("unregister event(current is $endEvent) can't be earlier than register event(current is $startEvent)")
        }
    }

    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            startEvent -> NetworkUtil.registerNetworkStatusListener(this)
            endEvent -> NetworkUtil.unregisterNetworkStatusListener(this)
            else -> {}
        }
    }
}