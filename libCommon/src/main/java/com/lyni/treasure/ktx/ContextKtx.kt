@file:Suppress("unused", "DEPRECATION")

package com.lyni.treasure.ktx

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Process
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import com.lyni.treasure.utils.Utils
import kotlin.system.exitProcess


val appContext: Context get() = Utils.getAppContext()

inline fun <reified A : Activity> Context.startActivity(configIntent: Intent.() -> Unit = {}) {
    val intent = Intent(this, A::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.apply(configIntent)
    startActivity(intent)
}

inline fun <reified T : Service> Context.startService(configIntent: Intent.() -> Unit = {}) {
    startService(Intent(this, T::class.java).apply(configIntent))
}

inline fun <reified T : Service> Context.stopService() {
    stopService(Intent(this, T::class.java))
}

fun Context.restart() {
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
    intent?.let {
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(intent)
        //杀掉以前进程
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}

/**
 * 系统息屏时间
 */
fun Context.getSysScreenOffTime(): Int {
    return kotlin.runCatching {
        Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
    }.onFailure {
        it.printOnDebug()
    }.getOrDefault(0)
}

fun Context.getScreenSize(): DisplayMetrics = this.resources.displayMetrics

fun Context.getRealScreenSize(): DisplayMetrics {
    val outMetrics = DisplayMetrics()
    getWindowManager().defaultDisplay.getRealMetrics(outMetrics)
    return outMetrics
}

fun Context.getWindowManager() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

fun Context.isDarkTheme(): Boolean {
    val flag: Int = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return flag == Configuration.UI_MODE_NIGHT_YES
}
