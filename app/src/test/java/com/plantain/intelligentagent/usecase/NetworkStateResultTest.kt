package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
/**
 * 网络状态测试
 * WiFi、5G、以太网被认为是强网络，优先使用云端模型。
 * 2G、3G 被认为是弱网络，优先使用本地模型。
 * 离线状态被认为是弱网络，优先使用本地模型。
 */
class NetworkStateResultTest {

    private fun state(type: NetworkType, connected: Boolean = true, online: Boolean = true) =
        NetworkStateResult(type = type, isConnected = connected, isOnline = online)

    /**
     * WiFi、5G、以太网被认为是强网络
     */
    @Test
    fun wifi_isStrongNetwork() {
        assertTrue(state(NetworkType.WIFI).isStrongNetwork)
    }

    /**
     * 5G 被认为是强网络
     */
    @Test
    fun cellular5g_isStrongNetwork() {
        assertTrue(state(NetworkType.CELLULAR_5G).isStrongNetwork)
    }

    /**
     * 以太网被认为是强网络
     */
    @Test
    fun ethernet_isStrongNetwork() {
        assertTrue(state(NetworkType.ETHERNET).isStrongNetwork)
    }

    /**
     * 4G、3G、2G 被认为是弱网络
     */
    @Test
    fun cellular4g_3g_2g_areNotStrong() {
        assertFalse(state(NetworkType.CELLULAR_4G).isStrongNetwork)
        assertFalse(state(NetworkType.CELLULAR_3G).isStrongNetwork)
        assertFalse(state(NetworkType.CELLULAR_2G).isStrongNetwork)
        assertFalse(state(NetworkType.NONE).isStrongNetwork)
    }

    /**
     * 离线状态被认为是弱网络
     */
    @Test
    fun offline_isOfflineOrWeak() {
        assertTrue(state(NetworkType.WIFI, online = false).isOfflineOrWeakNetwork)
    }

    /**
     * 2G、3G 被认为是弱网络
     */
    @Test
    fun cellular2gAnd3g_areWeak() {
        assertTrue(state(NetworkType.CELLULAR_2G).isOfflineOrWeakNetwork)
        assertTrue(state(NetworkType.CELLULAR_3G).isOfflineOrWeakNetwork)
    }

    /**
     * WiFi、4G、5G 被认为是强网络
     */
    @Test
    fun wifi_4g_5g_areNotWeak_whenOnline() {
        assertFalse(state(NetworkType.WIFI).isOfflineOrWeakNetwork)
        assertFalse(state(NetworkType.CELLULAR_4G).isOfflineOrWeakNetwork)
        assertFalse(state(NetworkType.CELLULAR_5G).isOfflineOrWeakNetwork)
    }

    /**
     * 未解析的网络类型被认为是弱网络
     */
    @Test
    fun unresolvedNetwork_isNotStrong_andIsOffline() {
        val s = NetworkStateResult(NetworkType.NONE, isConnected = false, isOnline = false)
        assertFalse(s.isStrongNetwork)
        assertTrue(s.isOfflineOrWeakNetwork)
    }

    /**
     * 测试属性的正确性
     */
    @Test
    fun properties_roundTrip() {
        val s = state(NetworkType.WIFI)
        assertEquals(NetworkType.WIFI, s.type)
        assertEquals(true, s.isConnected)
        assertEquals(true, s.isOnline)
    }
}