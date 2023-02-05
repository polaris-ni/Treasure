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
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
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
 * description network util
 */
object NetworkUtil {

    /**
     * is network connected
     * @return true or false
     */
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun isNetworkConnected(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities: NetworkCapabilities? =
                connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
            networkCapabilities != null && hasCapabilities(
                NetworkCapabilities.NET_CAPABILITY_INTERNET, NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        } else {
            val networkInfo = connectivityManager?.activeNetworkInfo as NetworkInfo
            networkInfo.isConnected && networkInfo.isAvailable
        }
    }

    /**
     * is dns working
     * @param domain domain
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
     * is mobile data enabled
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
     * is using vpn
     *
     * @return true or false
     */
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun isUsingVPN(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasTransports(NetworkCapabilities.TRANSPORT_VPN)
    } else {
        getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting
    }

    /**
     * is using cellular
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isUsingCellular(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasTransports(NetworkCapabilities.TRANSPORT_CELLULAR)
    } else {
        val info = getActiveNetworkInfo()
        (info != null) && info.isAvailable && (info.type == ConnectivityManager.TYPE_MOBILE)
    }

    /**
     * is 4G
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = getActiveNetworkInfo()
        return (info != null) && info.isConnected && (info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
    }

    /**
     * is 5G, NSA will be treat as 4G
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is5G(): Boolean {
        val info = getActiveNetworkInfo()
        return (info != null) && info.isAvailable && (info.subtype == TelephonyManager.NETWORK_TYPE_NR)
    }

    /**
     * is wifi enabled
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_WIFI_STATE)
    fun isWifiEnabled() = getWifiManager().isWifiEnabled

    /**
     * set wifi enabled
     *
     * @param enabled 是否启用
     */
    @RequiresPermission(permission.CHANGE_WIFI_STATE)
    fun setWifiEnabled(enabled: Boolean) {
        val manager = getWifiManager()
        if (enabled == manager.isWifiEnabled) {
            return
        }
        manager.isWifiEnabled = enabled
    }

    /**
     * is wifi connected
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isWifiConnected(): Boolean {
        val ni = getConnectivityManager().activeNetworkInfo
        return (ni != null) && (ni.type == ConnectivityManager.TYPE_WIFI)
    }

    /**
     * is using ethernet
     *
     * @return true or false
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isEthernet(): Boolean {
        val info = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
        val state = info.state ?: return false
        return (state == NetworkInfo.State.CONNECTED) || (state == NetworkInfo.State.CONNECTING)
    }

    /**
     * is using bluetooth
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isBluetooth(): Boolean = hasTransports(NetworkCapabilities.TRANSPORT_BLUETOOTH)

    /**
     * is using usb
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isUSB(): Boolean = hasTransports(NetworkCapabilities.TRANSPORT_USB)

    /**
     * is using LoWPAN
     *
     * @return true or false
     */
    @RequiresApi(Build.VERSION_CODES.O_MR1)
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isLoWPAN(): Boolean = hasTransports(NetworkCapabilities.TRANSPORT_LOWPAN)

    /**
     * obtain the name of network operator
     *
     * @return name
     */
    fun getNetworkOperatorName(): String {
        val tm = getAppContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName
    }

    /**
     * obtain network type
     *
     * @return [NetworkType] - type
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
     * obtain ip address
     *
     * @param useIPv4 is using ipv4
     * @return ip address
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
                            return if (index < 0) hostAddress.uppercase(Locale.getDefault())
                            else hostAddress.substring(0, index).uppercase(Locale.getDefault())
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
     * obtain SSID，require ACCESS_WIFI_STATE ACCESS_NETWORK_STATE ACCESS_FINE_LOCATION
     *
     * @return SSID
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_NETWORK_STATE, permission.ACCESS_FINE_LOCATION])
    fun getSSID(): String {
        val wm = getAppContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ssid = wm.connectionInfo?.ssid
        if (ssid.isNullOrEmpty()) {
            return ""
        }
        return if (ssid.length > 2 && ssid[0] == '"' && ssid[ssid.length - 1] == '"') {
            ssid.substring(1, ssid.length - 1)
        } else ssid
    }

    /**
     * register network status listener
     *
     * @param listener [NetworkStatusListener] - listener
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun registerNetworkStatusListener(listener: NetworkStatusListener) {
        NetworkChangedReceiver.registerListener(listener)
    }

    /**
     * is listener registered
     *
     * @param listener [NetworkStatusListener] - 监听器
     * @return true or false
     */
    fun isRegisteredNetworkStatusListener(listener: NetworkStatusListener): Boolean {
        return NetworkChangedReceiver.isRegistered(listener)
    }

    /**
     * unregister network status listener
     *
     * @param listener [NetworkStatusListener] - listener
     */
    fun unregisterNetworkStatusListener(listener: NetworkStatusListener) {
        NetworkChangedReceiver.unregisterListener(listener)
    }

    /**
     * scan wifi
     *
     * @return [ScanResult] - results
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    fun getWifiScanResult(): List<ScanResult> {
        if (!isWifiEnabled()) return listOf()
        return getWifiManager().scanResults ?: listOf()
    }

    /**
     * obtain wifi manager instance
     *
     * @return [WifiManager] - WifiManager
     */
    fun getWifiManager(): WifiManager =
        getAppContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    /**
     * get connectivity manager instance
     *
     * @return [ConnectivityManager] - ConnectivityManager
     */
    fun getConnectivityManager(): ConnectivityManager =
        getAppContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * [NetworkCapabilities.hasCapability]
     *
     * @param capabilities capabilities
     * @return true or false
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
     * [NetworkCapabilities.hasTransport]
     *
     * @param transports transports
     * @return true or false
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

    /**
     * register [ConnectivityManager.NetworkCallback],
     *
     * @param callback cb
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun registerCallback(callback: ConnectivityManager.NetworkCallback) {
        getConnectivityManager().registerDefaultNetworkCallback(callback)
    }

    /**
     * unregister [ConnectivityManager.NetworkCallback]
     *
     * @param callback cb
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    fun unregisterCallback(callback: ConnectivityManager.NetworkCallback) {
        getConnectivityManager().unregisterNetworkCallback(callback)
    }
}