package com.plantain.intelligentagent.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager

/**
 * 使用案例检查设备的当前网络状态。
 */
class NetworkStateUseCase(private val context: Context) {

    fun execute(): NetworkStateResult {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetwork
            ?: return NetworkStateResult(NetworkType.NONE, isConnected = false, isOnline = false)

        val capabilities = cm.getNetworkCapabilities(activeNetwork)
            ?: return NetworkStateResult(NetworkType.NONE, isConnected = false, isOnline = false)

        val type = resolveNetworkType(capabilities)
        val isConnected = cm.getNetworkInfo(activeNetwork)?.isConnected ?: true
        val isOnline = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return NetworkStateResult(type, isConnected, isOnline)
    }

    private fun resolveNetworkType(capabilities: NetworkCapabilities): NetworkType {
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                resolveCellularType()
            else -> NetworkType.NONE
        }
    }

    private fun resolveCellularType(): NetworkType {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G
                TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_4G
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> NetworkType.CELLULAR_3G
                else -> NetworkType.CELLULAR_2G
            }
        } catch (e: Exception) {
            // 获取蜂窝网络类型失败时兜底为 4G
            NetworkType.CELLULAR_4G
        }
    }
}