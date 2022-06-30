@file:Suppress("DEPRECATION")

package com.lyni.treasure.arch.network

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import com.lyni.treasure.ktx.runOnUiThread
import com.lyni.treasure.ktx.runOnUiThreadDelayed
import com.lyni.treasure.utils.Utils

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description [NetworkChangedReceiver]网络变化广播接收器
 */
class NetworkChangedReceiver : BroadcastReceiver() {

    companion object {
        val instance = NetworkChangedReceiver()
    }

    private var lastNetworkType: NetworkType? = null
    private val mListeners = HashSet<NetworkStatusListener>()

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @Synchronized
    fun registerListener(listener: NetworkStatusListener) {
        runOnUiThread {
            val preSize = mListeners.size
            mListeners.add(listener)
            if (preSize == 0 && mListeners.size == 1) {
                lastNetworkType = NetworkUtil.getNetworkType()
                val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                Utils.getAppContext().registerReceiver(instance, intentFilter)
            }
        }
    }

    fun isRegistered(listener: NetworkStatusListener): Boolean {
        return mListeners.contains(listener)
    }

    fun unregisterListener(listener: NetworkStatusListener) {
        runOnUiThread {
            val preSize = mListeners.size
            mListeners.remove(listener)
            if (preSize == 1 && mListeners.size == 0) {
                Utils.getAppContext().unregisterReceiver(instance)
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            // 延迟，防止抖动
            runOnUiThreadDelayed(1000) {
                val currentNetworkType = NetworkUtil.getNetworkType()
                for (listener in mListeners) {
                    listener.onReceiveStatus(currentNetworkType, lastNetworkType!!)
                }
                lastNetworkType = currentNetworkType
            }
        }
    }
}