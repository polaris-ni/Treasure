package com.lyni.treasure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lyni.treasure.lib.common.components.fitNavigationBar
import com.lyni.treasure.lib.common.components.fitStatusBar
import com.lyni.treasure.lib.common.components.immersiveNavigationBar
import com.lyni.treasure.lib.common.components.immersiveStatusBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersiveNavigationBar()
        immersiveStatusBar()
        fitStatusBar(true)
        fitNavigationBar(true)
    }
}