package com.lyni.treasure.utils

import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.BatteryManager
import com.lyni.treasure.ktx.appContext

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description [BaseInfoUtil]
 */
object BaseInfoUtil {
    /**
     * 获取电量
     */
    fun getSysBattery(): Int {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = appContext.applicationContext.registerReceiver(null, iFilter)
        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

    fun isPad(): Boolean {
        return (appContext.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}