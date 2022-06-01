package com.lyni.treasure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lyni.treasure.databinding.ActivityMainBinding
import com.lyni.treasure.lib.common.components.fitNavigationBar
import com.lyni.treasure.lib.common.components.fitStatusBar
import com.lyni.treasure.lib.common.components.immersiveNavigationBar
import com.lyni.treasure.lib.common.components.immersiveStatusBar
import com.lyni.treasure.lib.common.utils.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveNavigationBar()
        immersiveStatusBar()
        fitStatusBar(true)
        fitNavigationBar(true)
        Log.openDebug()
    }
}