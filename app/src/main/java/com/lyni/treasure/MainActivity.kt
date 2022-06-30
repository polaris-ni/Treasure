package com.lyni.treasure

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lyni.treasure.arch.network.NetworkUtil
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveNavigationBar()
        immersiveStatusBar()
        fitStatusBar(true)
        fitNavigationBar(true)
        Log.openDebug()
//        Log.e(TAG, "onCreate: ${getDisplayMetrics().widthPixels} ${getDisplayMetrics().heightPixels}")
//        Log.e(TAG, "onCreate: ${getRealDisplayMetrics().widthPixels} ${getRealDisplayMetrics().heightPixels}")
//        Log.e(TAG, "onCreate: ${BaseInfoUtil.getSysBattery()} ${BaseInfoUtil.isPad()}")
//        NetworkUtil.getActiveNetwork()

        NetworkUtil.getConnectivityManager()
            .registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.e(TAG, "onAvailable: $network")
                }

                override fun onLost(network: Network) {
                    Log.e(TAG, "onLost: $network")
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    Log.e(TAG, "onCapabilitiesChanged: $networkCapabilities")
                }

                override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                    Log.e(TAG, "onLinkPropertiesChanged: $linkProperties")
                }
            })
    }
}