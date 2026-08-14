package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkStateResultTest {

    private fun state(type: NetworkType, connected: Boolean = true, online: Boolean = true) =
        NetworkStateResult(type = type, isConnected = connected, isOnline = online)

    @Test
    fun wifi_isStrongNetwork() {
        assertTrue(state(NetworkType.WIFI).isStrongNetwork)
    }

    @Test
    fun cellular5g_isStrongNetwork() {
        assertTrue(state(NetworkType.CELLULAR_5G).isStrongNetwork)
    }

    @Test
    fun ethernet_isStrongNetwork() {
        assertTrue(state(NetworkType.ETHERNET).isStrongNetwork)
    }

    @Test
    fun cellular4g_3g_2g_areNotStrong() {
        assertFalse(state(NetworkType.CELLULAR_4G).isStrongNetwork)
        assertFalse(state(NetworkType.CELLULAR_3G).isStrongNetwork)
        assertFalse(state(NetworkType.CELLULAR_2G).isStrongNetwork)
        assertFalse(state(NetworkType.NONE).isStrongNetwork)
    }

    @Test
    fun offline_isOfflineOrWeak() {
        assertTrue(state(NetworkType.WIFI, online = false).isOfflineOrWeakNetwork)
    }

    @Test
    fun cellular2gAnd3g_areWeak() {
        assertTrue(state(NetworkType.CELLULAR_2G).isOfflineOrWeakNetwork)
        assertTrue(state(NetworkType.CELLULAR_3G).isOfflineOrWeakNetwork)
    }

    @Test
    fun wifi_4g_5g_areNotWeak_whenOnline() {
        assertFalse(state(NetworkType.WIFI).isOfflineOrWeakNetwork)
        assertFalse(state(NetworkType.CELLULAR_4G).isOfflineOrWeakNetwork)
        assertFalse(state(NetworkType.CELLULAR_5G).isOfflineOrWeakNetwork)
    }

    @Test
    fun unresolvedNetwork_isNotStrong_andIsOffline() {
        val s = NetworkStateResult(NetworkType.NONE, isConnected = false, isOnline = false)
        assertFalse(s.isStrongNetwork)
        assertTrue(s.isOfflineOrWeakNetwork)
    }

    @Test
    fun properties_roundTrip() {
        val s = state(NetworkType.WIFI)
        assertEquals(NetworkType.WIFI, s.type)
        assertEquals(true, s.isConnected)
        assertEquals(true, s.isOnline)
    }
}