package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class InferenceModeResolverTest {

    private val strongNetwork = NetworkStateResult(NetworkType.WIFI, isConnected = true, isOnline = true)
    private val weakNetwork = NetworkStateResult(NetworkType.CELLULAR_3G, isConnected = true, isOnline = true)
    private val offlineNetwork = NetworkStateResult(NetworkType.WIFI, isConnected = false, isOnline = false)
    private val normalNetwork = NetworkStateResult(NetworkType.CELLULAR_4G, isConnected = true, isOnline = true)

    private val plentyResources = DeviceResourceResult(
        availableMemoryMb = 4096,
        totalMemoryMb = 8192,
        cpuUsagePercent = 10f,
        batteryPercent = 80,
        batteryTemperature = 30f
    )
    private val lowResources = plentyResources.copy(availableMemoryMb = 512)

    private fun resolve(
        network: NetworkStateResult = strongNetwork,
        resource: DeviceResourceResult = plentyResources,
        serviceAvailable: Boolean = false,
        serviceBound: Boolean = false,
        localModelLoaded: Boolean = false
    ) = InferenceModeResolver.resolve(
        network = network,
        resource = resource,
        serviceAvailable = serviceAvailable,
        serviceBound = serviceBound,
        localModelLoaded = localModelLoaded
    )

    @Test
    fun serviceReady_takesHighestPriority() {
        // Even with low resources / strong network, service wins when available & bound.
        assertEquals("service", resolve(serviceAvailable = true, serviceBound = true))
        assertEquals(
            "service",
            resolve(network = offlineNetwork, serviceAvailable = true, serviceBound = true)
        )
    }

    @Test
    fun serviceNotBound_fallsThrough() {
        // 强网下即使有本地模型，云端仍优先（精度保障）
        assertEquals("network", resolve(serviceAvailable = true, serviceBound = false, localModelLoaded = false))
        assertEquals("network", resolve(serviceAvailable = true, serviceBound = false, localModelLoaded = true))
        // 普通网络下服务未绑定 → 本地优先
        assertEquals(
            "local",
            resolve(network = normalNetwork, serviceAvailable = true, serviceBound = false, localModelLoaded = true)
        )
    }

    @Test
    fun offlineWithLocalModel_prefersLocal() {
        assertEquals("local", resolve(network = offlineNetwork, localModelLoaded = true))
    }

    @Test
    fun offlineWithoutLocalModel_fallsBackToCloud() {
        assertEquals("network", resolve(network = offlineNetwork, localModelLoaded = false))
    }

    @Test
    fun weakNetworkWithLocalModel_prefersLocal() {
        assertEquals("local", resolve(network = weakNetwork, localModelLoaded = true))
    }

    @Test
    fun lowResources_prefersCloudWhenOnline() {
        assertEquals("network", resolve(resource = lowResources))
    }

    @Test
    fun lowResourcesWithOfflineNetwork_doesNotUseCloud() {
        // Offline branch wins before the resource branch.
        assertEquals("local", resolve(network = offlineNetwork, resource = lowResources, localModelLoaded = true))
    }

    @Test
    fun strongNetwork_prefersCloud() {
        assertEquals("network", resolve(network = strongNetwork, localModelLoaded = true))
    }

    @Test
    fun strongNetwork_sufficientResources_usesCloud() {
        assertEquals("network", resolve())
    }

    @Test
    fun normalNetwork_withLocalModel_prefersLocal() {
        assertEquals("local", resolve(network = normalNetwork, localModelLoaded = true))
    }

    @Test
    fun normalNetwork_noLocalModel_fallsBackToCloud() {
        assertEquals("network", resolve(network = normalNetwork, localModelLoaded = false))
    }
}