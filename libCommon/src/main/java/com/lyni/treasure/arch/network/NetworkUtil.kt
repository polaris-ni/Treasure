@file:Suppress("UNUSED", "DEPRECATION", "MemberVisibilityCanBePrivate")

package com.lyni.treasure.arch.network

import android.Manifest.permission
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.lyni.treasure.interfaces.NetworkChangeListener
import com.lyni.treasure.ktx.appContext
import com.lyni.treasure.ktx.negative
import com.lyni.treasure.utils.Utils.getAppContext
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.util.*


/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description 网络相关工具类
 */
object NetworkUtil {

    /**
     * 当前是否连接网络
     * @return true or false
     */
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun isNetworkConnected(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities: NetworkCapabilities? =
                connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
            networkCapabilities != null && hasCapabilities(
                NetworkCapabilities.NET_CAPABILITY_INTERNET,
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        } else {
            val networkInfo = connectivityManager?.activeNetworkInfo as NetworkInfo
            networkInfo.isConnected && networkInfo.isAvailable
        }
    }

    /**
     * 解析某个域名的ip地址判断DNS是否可用
     * @param domain 域名
     * @return true or false
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDns(domain: String): Boolean {
        val inetAddress: InetAddress?
        return try {
            inetAddress = InetAddress.getByName(domain)
            inetAddress != null
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断手机数据是否启用
     *
     * @return true or false
     */
    @RequiresPermission(anyOf = [permission.ACCESS_NETWORK_STATE, permission.MODIFY_PHONE_STATE, permission.READ_PHONE_STATE])
    fun isMobileDataEnabled(): Boolean {
        try {
            val tm = getAppContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled
            }
            val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
            return getMobileDataEnabledMethod.invoke(tm) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 是否启动代理
     *
     * @return true or false
     */
    fun isProxyEnabled(): Boolean =
        !(System.getProperty("http.proxyHost") == null || System.getProperty("http.proxyPort") == null)

    /**
     * 判断网络是否通过VPN连接
     *
     * @return true or false
     */
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun isUsingVPN(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasTransports(NetworkCapabilities.TRANSPORT_VPN)
    } else {
        val cm = getConnectivityManager()
        cm.getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting
    }

    /**
     * 是否使用蜂窝网络
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isUsingCellular(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasTransports(NetworkCapabilities.TRANSPORT_CELLULAR)
    } else {
        val info = getActiveNetworkInfo()
        null != info && info.isAvailable && info.type == ConnectivityManager.TYPE_MOBILE
    }

    /**
     * 是否是4G网络
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = getActiveNetworkInfo()
        return (info != null && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
    }

    /**
     * 是否是5G网络
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is5G(): Boolean {
        val info = getActiveNetworkInfo()
        return (info != null && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_NR)
    }

    /**
     * 是否启用Wifi
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_WIFI_STATE)
    fun isWifiEnabled() = getWifiManager().isWifiEnabled

    /**
     * 控制Wifi是否启用
     *
     * @param enabled 是否启用
     */
    @RequiresPermission(permission.CHANGE_WIFI_STATE)
    fun setWifiEnabled(enabled: Boolean) {
        val manager = getWifiManager()
        if (enabled == manager.isWifiEnabled) return
        manager.isWifiEnabled = enabled
    }

    /**
     * 是否连接Wifi
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isWifiConnected(): Boolean {
        val ni = getConnectivityManager().activeNetworkInfo
        return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * 是否是以太网
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isEthernet(): Boolean {
        val info = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
        val state = info.state ?: return false
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
    }

    /**
     * 是否通过蓝牙连接网络
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isBluetooth(): Boolean = hasTransports(NetworkCapabilities.TRANSPORT_BLUETOOTH)

    /**
     * 是否通过USB连接网络
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isUSB(): Boolean = hasTransports(NetworkCapabilities.TRANSPORT_USB)

    /**
     * 是否通过 LoWPAN 连接网络
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.O_MR1)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isLoWPAN(): Boolean = hasTransports(NetworkCapabilities.TRANSPORT_LOWPAN)

    /**
     * 获取network operator 名称
     *
     * @return name
     */
    fun getNetworkOperatorName(): String {
        val tm = getAppContext()
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName
    }

    /**
     * 获取网络类型
     *
     * @return [NetworkType] - 网络类型
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun getNetworkType(): NetworkType {
        if (isEthernet()) {
            return NetworkType.ETHERNET
        }
        val info = getActiveNetworkInfo()
        return if (info != null && info.isAvailable) {
            when (info.type) {
                ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                ConnectivityManager.TYPE_MOBILE -> {
                    when (info.subtype) {
                        TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.CELLULAR_2G
                        TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.CELLULAR_3G
                        TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_4G
                        TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G
                        else -> {
                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NetworkType.CELLULAR_3G
                            } else {
                                NetworkType.UNKNOWN
                            }
                        }
                    }
                }
                else -> NetworkType.UNKNOWN
            }
        } else NetworkType.NONE
    }

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private fun getActiveNetworkInfo(): NetworkInfo? {
        return getConnectivityManager().activeNetworkInfo
    }

    /**
     * 获取ip地址
     *
     * @param useIPv4 是否使用IPv4
     * @return ip地址
     */
    @RequiresPermission(permission.INTERNET)
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress ?: return ""
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return hostAddress
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0) hostAddress.uppercase(Locale.getDefault()) else hostAddress.substring(
                                0,
                                index
                            ).uppercase(
                                Locale.getDefault()
                            )
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取SSID
     *
     * @return SSID
     */
    @RequiresPermission(permission.ACCESS_WIFI_STATE)
    fun getSSID(): String {
        val wm = getAppContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wi = wm.connectionInfo ?: return ""
        val ssid = wi.ssid
        if (TextUtils.isEmpty(ssid)) {
            return ""
        }
        return if (ssid.length > 2 && ssid[0] == '"' && ssid[ssid.length - 1] == '"') {
            ssid.substring(1, ssid.length - 1)
        } else ssid
    }

    /**
     * 注册网络变化监听器
     *
     * @param listener [NetworkChangeListener] - 监听器
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun registerNetworkStatusChangedListener(listener: NetworkChangeListener) {
        NetworkChangedReceiver.instance.registerListener(listener)
    }

    /**
     * 判断网络变化监听器是否注册
     *
     * @param listener [NetworkChangeListener] - 监听器
     * @return true or false
     */
    fun isRegisteredNetworkStatusChangedListener(listener: NetworkChangeListener): Boolean {
        return NetworkChangedReceiver.instance.isRegistered(listener)
    }

    /**
     * 注销网络变化监听器
     *
     * @param listener [NetworkChangeListener] - 监听器
     */
    fun unregisterNetworkStatusChangedListener(listener: NetworkChangeListener) {
        NetworkChangedReceiver.instance.unregisterListener(listener)
    }

    /**
     * 获取wifi扫描结果
     *
     * @return [ScanResult] - Wifi扫描结果
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
    fun getWifiScanResult(): List<ScanResult> {
        if (!isWifiEnabled()) return listOf()
        return getWifiManager().scanResults ?: listOf()
    }

    /**
     * 获取wifi manager
     *
     * @return [WifiManager] - WifiManager对象
     */
    fun getWifiManager(): WifiManager =
        getAppContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    /**
     * 获取connectivity manager
     *
     * @return [ConnectivityManager] - ConnectivityManager对象
     */
    fun getConnectivityManager(): ConnectivityManager =
        getAppContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * 判断满足[NetworkCapabilities.hasCapability]
     *
     * @param capabilities 需要判断的内容
     * @return 是否全部存在
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun hasCapabilities(vararg capabilities: Int): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkCapabilities: NetworkCapabilities? =
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (networkCapabilities != null) {
            capabilities.forEach {
                networkCapabilities.hasCapability(it).negative {
                    return false
                }
            }
            return true
        } else {
            return false
        }
    }

    /**
     * 判断满足[NetworkCapabilities.hasTransport]
     *
     * @param transports 需要判断的内容
     * @return 是否全部存在
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun hasTransports(vararg transports: Int): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkCapabilities: NetworkCapabilities? =
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (networkCapabilities != null) {
            transports.forEach {
                networkCapabilities.hasTransport(it).negative {
                    return false
                }
            }
            return true
        } else {
            return false
        }
    }
}