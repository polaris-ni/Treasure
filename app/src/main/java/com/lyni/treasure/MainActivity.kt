package com.lyni.treasure

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lyni.treasure.arch.network.NetworkChangeLifecycleListener
import com.lyni.treasure.arch.network.NetworkType
import com.lyni.treasure.components.fitNavigationBar
import com.lyni.treasure.components.fitStatusBar
import com.lyni.treasure.components.immersiveNavigationBar
import com.lyni.treasure.components.immersiveStatusBar
import com.lyni.treasure.databinding.ActivityMainBinding
import com.lyni.treasure.utils.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
    }

    private val listener = object : NetworkChangeLifecycleListener() {
        override fun onChanged(type: NetworkType) {
            Log.e(TAG, "onChanged: $type ")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveNavigationBar()
        immersiveStatusBar()
        fitStatusBar(true)
        fitNavigationBar(true)
        listener.observe(this.lifecycle)
    }
}