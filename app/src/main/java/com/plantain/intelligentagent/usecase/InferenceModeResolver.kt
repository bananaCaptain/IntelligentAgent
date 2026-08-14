package com.plantain.intelligentagent.usecase

/**
 * 根据网络状态、设备资源与服务可用性，自动选择推理模式。
 * 纯函数形式便于单元测试。
 */
object InferenceModeResolver {

    fun resolve(
        network: NetworkStateResult,
        resource: DeviceResourceResult,
        serviceAvailable: Boolean,
        serviceBound: Boolean,
        localModelLoaded: Boolean
    ): String {
        val serviceReady = serviceAvailable && serviceBound

        return when {
            // 服务可用且已绑定：优先复用共享服务模型，减少设备资源冗余
            serviceReady -> "service"
            // 弱网或离线：优先本地模型，其次才尝试云端
            network.isOfflineOrWeakNetwork ->
                if (localModelLoaded) "local" else "network"
            // 设备资源不足（内存/电量/温度）：优先云端模型，降低本地算力消耗与发热
            resource.shouldUseCloudModel && network.isOnline -> "network"
            // 强网环境（WiFi/5G）：优先云端模型，保障信息提取精度
            network.isStrongNetwork -> "network"
            // 资源充足且网络一般：优先本地推理
            localModelLoaded -> "local"
            // 兜底
            else -> "network"
        }
    }
}