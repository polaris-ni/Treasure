package com.lyni.treasure.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lyni.permission.QuickPermission
import com.lyni.permission.core.Permissions
import com.lyni.treasure.arch.network.NetworkChangeLifecycleListener
import com.lyni.treasure.arch.network.NetworkType
import com.lyni.treasure.arch.network.NetworkUtil
import com.lyni.treasure.databinding.ActivityMainBinding
import com.lyni.treasure.ktx.checkSDK
import com.lyni.treasure.ktx.ioLaunch
import com.lyni.treasure.ktx.mainHandler
import com.lyni.treasure.ktx.mainLaunch
import com.lyni.treasure.ui.adapter.TestAdapter
import com.lyni.treasure.utils.Log
import com.lyni.treasure.utils.showToast
import com.lyni.treasure.vm.model.TestItem
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val testItems = mutableListOf<TestItem>()

    companion object {
        private const val TAG = "MainActivity"
    }

    private val listener = object : NetworkChangeLifecycleListener() {
        override fun onChanged(type: NetworkType) {
            Log.e(TAG, "onChanged: $type ")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        immersiveNavigationBar()
//        immersiveStatusBar()
//        fitStatusBar(true)
//        fitNavigationBar(true)
//        setNavigationBarColor(Color.BLUE)
//        setLightStatusBar(!isDarkTheme())
//        setLightNavigationBar(false)
        listener.observe(this.lifecycle)
        initTestItems()
        binding.rvTestItem.adapter = TestAdapter().apply {
            addItems(testItems)
            setOnItemClickListener { _, _, position ->
                removeItem(position)
            }
            setOnItemLongClickListener { _, item, _ ->
                showToast("${item.action}")
            }
            addFooterView { _, _ ->
                FrameLayout(this@MainActivity).apply {
                    addView(TextView(this@MainActivity).apply {
                        text = "hello, my base rv adapter!"
                    })
                    setBackgroundColor(Color.BLUE)
//                    layoutParams =
//                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }

            }
        }
        mainLaunch {
            delay(5000)
            (binding.rvTestItem.adapter as TestAdapter).removeItem(testItems[0])
            testItems[1].name = "111"
            (binding.rvTestItem.adapter as TestAdapter).updateItem(testItems[1])
            (binding.rvTestItem.adapter as TestAdapter).swapItem(0, 1)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initTestItems() {
        testItems.apply {
            add(TestItem("??????????????????") { Log.e(TAG, "??????????????????: ${NetworkUtil.isNetworkConnected()}") })
            add(TestItem("??????????????????") {
                ioLaunch {
                    Log.e(TAG, "??????????????????: ${NetworkUtil.isAvailableByDns("www.baidu.com")}")
                }
            })
            add(TestItem("????????????????????????") {
                QuickPermission.with(this@MainActivity).addPermissions(Permissions.READ_PHONE_STATE).onGranted {
                    Log.e(TAG, "????????????????????????: ${NetworkUtil.isMobileDataEnabled()}")
                }.request()
            })
            add(TestItem("????????????VPN") { Log.e(TAG, "????????????VPN: ${NetworkUtil.isUsingVPN()}") })
            add(TestItem("??????????????????") { Log.e(TAG, "??????????????????: ${NetworkUtil.isUsingCellular()}") })
            add(TestItem("????????????4G") { Log.e(TAG, "????????????4G: ${NetworkUtil.is4G()}") })
            checkSDK(Build.VERSION_CODES.Q) {
                add(TestItem("????????????5G") { Log.e(TAG, "????????????5G: ${NetworkUtil.is5G()}") })
            }.onFailure { Log.e(TAG, "initTestItems: ?????????5G") }
            add(TestItem("????????????Wifi") { Log.e(TAG, "????????????Wifi: ${NetworkUtil.isWifiEnabled()}") })
            add(TestItem("????????????Wifi") { Log.e(TAG, "????????????Wifi: ${NetworkUtil.isWifiConnected()}") })
            add(TestItem("?????????????????????") { Log.e(TAG, "?????????????????????: ${NetworkUtil.isEthernet()}") })
            checkSDK(Build.VERSION_CODES.M) {
                add(TestItem("????????????????????????") { Log.e(TAG, "????????????????????????: ${NetworkUtil.isBluetooth()}") })
            }
            checkSDK(Build.VERSION_CODES.O_MR1) {
                add(TestItem("????????????LoWPAN??????") { Log.e(TAG, "????????????LoWPAN??????: ${NetworkUtil.isLoWPAN()}") })
            }
            add(TestItem("??????network operator ??????") {
                Log.e(
                    TAG,
                    "??????network operator ??????: ${NetworkUtil.getNetworkOperatorName()}"
                )
            })
            add(TestItem("??????????????????") { Log.e(TAG, "??????????????????: ${NetworkUtil.getNetworkType()}") })
            add(TestItem("??????IP??????") { Log.e(TAG, "??????IP??????: ${NetworkUtil.getIPAddress(true)}") })
            add(TestItem("??????SSID") { Log.e(TAG, "??????SSID: ${NetworkUtil.getSSID()}") })
            add(TestItem("??????wifi????????????") { Log.e(TAG, "??????wifi????????????: ${NetworkUtil.getWifiScanResult()}") })
        }.run {
            forEach {
                mainHandler.post(it.action)
            }
        }
    }
}