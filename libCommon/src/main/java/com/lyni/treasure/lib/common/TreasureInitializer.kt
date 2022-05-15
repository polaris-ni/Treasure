package com.lyni.treasure.lib.common

import android.content.Context
import androidx.startup.Initializer
import com.lyni.treasure.lib.common.utils.Utils

/**
 * @date 2022/5/15
 * @author Liangyong Ni
 * description TreasureInitializer
 */
class TreasureInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Utils.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}