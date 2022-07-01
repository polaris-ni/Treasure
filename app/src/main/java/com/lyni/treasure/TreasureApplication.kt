package com.lyni.treasure

import android.app.Application
import com.lyni.treasure.utils.Utils

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description [TreasureApplication]
 */
class TreasureApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.openDebug()
    }
}