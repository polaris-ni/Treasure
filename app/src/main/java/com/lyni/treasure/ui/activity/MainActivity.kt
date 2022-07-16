package com.lyni.treasure.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lyni.permission.QuickPermission
import com.lyni.permission.core.Permissions
import com.lyni.treasure.arch.network.NetworkChangeLifecycleListener
import com.lyni.treasure.arch.network.NetworkType
import com.lyni.treasure.arch.network.NetworkUtil
import com.lyni.treasure.components.*
import com.lyni.treasure.databinding.ActivityMainBinding
import com.lyni.treasure.ktx.*
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
        immersiveNavigationBar()
        immersiveStatusBar()
        fitStatusBar(true)
        fitNavigationBar(true)
        isDarkTheme().positive {
            setLightStatusBar(false)
            setLightNavigationBar(false)
        }.otherwise {
            setLightStatusBar(true)
            setLightNavigationBar(true)
        }
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
                TextView(this@MainActivity).apply {
                    text = "hello, my base rv adapter!"
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
            add(TestItem("测试网络连接") { Log.e(TAG, "测试网络连接: ${NetworkUtil.isNetworkConnected()}") })
            add(TestItem("测试域名解析") {
                ioLaunch {
                    Log.e(TAG, "测试域名解析: ${NetworkUtil.isAvailableByDns("www.baidu.com")}")
                }
            })
            add(TestItem("是否启用数据连接") {
                QuickPermission.with(this@MainActivity).addPermissions(Permissions.READ_PHONE_STATE).onGranted {
                    Log.e(TAG, "是否启用数据连接: ${NetworkUtil.isMobileDataEnabled()}")
                }.request()
            })
            add(TestItem("是否使用VPN") { Log.e(TAG, "是否使用VPN: ${NetworkUtil.isUsingVPN()}") })
            add(TestItem("是否使用数据") { Log.e(TAG, "是否使用数据: ${NetworkUtil.isUsingCellular()}") })
            add(TestItem("是否使用4G") { Log.e(TAG, "是否使用4G: ${NetworkUtil.is4G()}") })
            checkSDK(Build.VERSION_CODES.Q) {
                add(TestItem("是否使用5G") { Log.e(TAG, "是否使用5G: ${NetworkUtil.is5G()}") })
            }.onFailure { Log.e(TAG, "initTestItems: 不支持5G") }
            add(TestItem("是否启用Wifi") { Log.e(TAG, "是否启用Wifi: ${NetworkUtil.isWifiEnabled()}") })
            add(TestItem("是否连接Wifi") { Log.e(TAG, "是否连接Wifi: ${NetworkUtil.isWifiConnected()}") })
            add(TestItem("是否连接以太网") { Log.e(TAG, "是否连接以太网: ${NetworkUtil.isEthernet()}") })
            checkSDK(Build.VERSION_CODES.M) {
                add(TestItem("是否通过蓝牙联网") { Log.e(TAG, "是否通过蓝牙联网: ${NetworkUtil.isBluetooth()}") })
            }
            checkSDK(Build.VERSION_CODES.O_MR1) {
                add(TestItem("是否通过LoWPAN联网") { Log.e(TAG, "是否通过LoWPAN联网: ${NetworkUtil.isLoWPAN()}") })
            }
            add(TestItem("获取network operator 名称") {
                Log.e(
                    TAG,
                    "获取network operator 名称: ${NetworkUtil.getNetworkOperatorName()}"
                )
            })
            add(TestItem("获取网络类型") { Log.e(TAG, "获取网络类型: ${NetworkUtil.getNetworkType()}") })
            add(TestItem("获取IP地址") { Log.e(TAG, "获取IP地址: ${NetworkUtil.getIPAddress(true)}") })
            add(TestItem("获取SSID") { Log.e(TAG, "获取SSID: ${NetworkUtil.getSSID()}") })
            add(TestItem("获取wifi扫描结果") { Log.e(TAG, "获取wifi扫描结果: ${NetworkUtil.getWifiScanResult()}") })
        }.run {
            forEach {
                mainHandler.post(it.action)
            }
        }
    }
}