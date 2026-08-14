package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
/**
 * 设备资源状态测试
 * 内存低于 1024MB、剩余电量低于 20%、温度高于 45℃ 时，认为设备资源不足，应优先使用云端模型。
 *
 */
class DeviceResourceResultTest {

    private fun resource(
        availableMemoryMb: Long = 4096,
        totalMemoryMb: Long = 8192,
        cpuUsagePercent: Float = 10f,
        batteryPercent: Int = 80,
        batteryTemperature: Float = 30f
    ) = DeviceResourceResult(
        availableMemoryMb = availableMemoryMb,
        totalMemoryMb = totalMemoryMb,
        cpuUsagePercent = cpuUsagePercent,
        batteryPercent = batteryPercent,
        batteryTemperature = batteryTemperature
    )

    /**
     * 资源充足时，不使用云端模型
     */
    @Test
    fun sufficientResources_doNotUseCloud() {
        assertFalse(resource().isMemoryLow)
        assertFalse(resource().isBatteryLow)
        assertFalse(resource().isOverheated)
        assertFalse(resource().shouldUseCloudModel)
    }

    /**
     * 内存低于阈值时，认为内存不足，应使用云端模型
     */
    @Test
    fun memoryBelowThreshold_isLow() {
        val r = resource(availableMemoryMb = 1023)
        assertTrue(r.isMemoryLow)
        assertTrue(r.shouldUseCloudModel)
    }

    /**
     * 内存等于阈值时，认为内存充足，不使用云端模型
     */
    @Test
    fun memoryExactlyAtThreshold_isNotLow() {
        val r = resource(availableMemoryMb = 1024)
        assertFalse(r.isMemoryLow)
    }

    /**
     * 电量低于阈值时，认为电量不足，应使用云端模型
     */
    @Test
    fun batteryBelow20_isLow() {
        val r = resource(batteryPercent = 19)
        assertTrue(r.isBatteryLow)
        assertTrue(r.shouldUseCloudModel)
    }

    /**
     * 电量等于阈值时，认为电量充足，不使用云端模型
     */
    @Test
    fun batteryExactly20_isNotLow() {
        assertFalse(resource(batteryPercent = 20).isBatteryLow)
    }

    /**
     * 温度高于阈值时，认为设备过热，应使用云端模型
     */
    @Test
    fun temperatureAtOrAbove45_isOverheated() {
        assertTrue(resource(batteryTemperature = 45f).isOverheated)
        assertTrue(resource(batteryTemperature = 50f).shouldUseCloudModel)
    }

    /**
     * 温度低于阈值时，认为设备不过热，不使用云端模型
     */
    @Test
    fun temperatureBelow45_isNotOverheated() {
        assertFalse(resource(batteryTemperature = 44.9f).isOverheated)
    }

    /**
     * 任何单项资源不足时，都应使用云端模型
     */
    @Test
    fun anySingleResourceShortage_shouldUseCloud() {
        assertTrue(resource(availableMemoryMb = 100).shouldUseCloudModel)
        assertTrue(resource(batteryPercent = 5).shouldUseCloudModel)
        assertTrue(resource(batteryTemperature = 70f).shouldUseCloudModel)
    }

    /**
     * CPU 使用率高时，虽然不作为决策输入，但仍应报告给云端模型
     */
    @Test
    fun cpuUsageIsReportedButNotDecisionInput() {
        val r = resource(cpuUsagePercent = 99f)
        assertFalse(r.shouldUseCloudModel)
    }
}