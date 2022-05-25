package com.lyni.treasure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lyni.treasure.lib.common.components.fitNavigationBar
import com.lyni.treasure.lib.common.components.fitStatusBar
import com.lyni.treasure.lib.common.components.immersiveNavigationBar
import com.lyni.treasure.lib.common.components.immersiveStatusBar
import com.lyni.treasure.lib.common.utils.showToast
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersiveNavigationBar()
        immersiveStatusBar()
        fitStatusBar(true)
        fitNavigationBar(true)
    }

    override fun onStart() {
        super.onStart()
        thread {
            showToast("子线程实例化测试")
        }
    }
}