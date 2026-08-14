package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
/**
 * 推理模式解析器测试
 * 1. 服务可用且绑定 → service
 * 2. 弱网或离线 → local > network
 * 3. 设备资源不足 → network > local
 * 4. 强网环境 → network > local
 * 5. 资源充足且网络一般 → local > network
 */
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

    /**
     * 服务可用且绑定 → service
     */
    @Test
    fun serviceReady_takesHighestPriority() {
        // Even with low resources / strong network, service wins when available & bound.
        assertEquals("service", resolve(serviceAvailable = true, serviceBound = true))
        assertEquals(
            "service",
            resolve(network = offlineNetwork, serviceAvailable = true, serviceBound = true)
        )
    }

    /**
     * 服务可用但未绑定 → falls through to network/local
     */
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

    /**
     * 弱网或离线 → local > network
     */
    @Test
    fun offlineWithLocalModel_prefersLocal() {
        assertEquals("local", resolve(network = offlineNetwork, localModelLoaded = true))
    }

    /**
     * 弱网或离线 → local > network
     */
    @Test
    fun offlineWithoutLocalModel_fallsBackToCloud() {
        assertEquals("network", resolve(network = offlineNetwork, localModelLoaded = false))
    }

    /**
     * 弱网或离线 → local > network
     */
    @Test
    fun weakNetworkWithLocalModel_prefersLocal() {
        assertEquals("local", resolve(network = weakNetwork, localModelLoaded = true))
    }

    /**
     * 弱网或离线 → local > network
     */
    @Test
    fun lowResources_prefersCloudWhenOnline() {
        assertEquals("network", resolve(resource = lowResources))
    }

    /**
     * 弱网或离线 → local > network
     */
    @Test
    fun lowResourcesWithOfflineNetwork_doesNotUseCloud() {
        // Offline branch wins before the resource branch.
        assertEquals("local", resolve(network = offlineNetwork, resource = lowResources, localModelLoaded = true))
    }

    /**
     * 强网环境 → network > local
     */
    @Test
    fun strongNetwork_prefersCloud() {
        assertEquals("network", resolve(network = strongNetwork, localModelLoaded = true))
    }

    /**
     * 强网环境 → network > local
     */
    @Test
    fun strongNetwork_sufficientResources_usesCloud() {
        assertEquals("network", resolve())
    }

    /**
     * 资源充足且网络一般 → local > network
     */
    @Test
    fun normalNetwork_withLocalModel_prefersLocal() {
        assertEquals("local", resolve(network = normalNetwork, localModelLoaded = true))
    }

    /**
     * 资源充足且网络一般 → local > network
     */
    @Test
    fun normalNetwork_noLocalModel_fallsBackToCloud() {
        assertEquals("network", resolve(network = normalNetwork, localModelLoaded = false))
    }
}