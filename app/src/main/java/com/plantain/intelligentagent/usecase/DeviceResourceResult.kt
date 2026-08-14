package com.plantain.intelligentagent.usecase

data class DeviceResourceResult(
    val availableMemoryMb: Long,
    val totalMemoryMb: Long,
    val cpuUsagePercent: Float,
    val batteryPercent: Int,
    val batteryTemperature: Float
) {
    val isMemoryLow: Boolean
        get() = availableMemoryMb < LOW_MEMORY_THRESHOLD_MB

    val isBatteryLow: Boolean
        get() = batteryPercent < LOW_BATTERY_THRESHOLD_PERCENT

    val isOverheated: Boolean
        get() = batteryTemperature >= OVERHEAT_THRESHOLD_C

    /** 设备资源不足时优先调度云端模型，降低本地算力消耗与发热 */
    val shouldUseCloudModel: Boolean
        get() = isMemoryLow || isBatteryLow || isOverheated

    private companion object {
        const val LOW_MEMORY_THRESHOLD_MB = 1024L
        const val LOW_BATTERY_THRESHOLD_PERCENT = 20
        const val OVERHEAT_THRESHOLD_C = 45.0f
    }
}