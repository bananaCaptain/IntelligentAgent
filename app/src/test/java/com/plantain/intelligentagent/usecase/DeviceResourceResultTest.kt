package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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

    @Test
    fun sufficientResources_doNotUseCloud() {
        assertFalse(resource().isMemoryLow)
        assertFalse(resource().isBatteryLow)
        assertFalse(resource().isOverheated)
        assertFalse(resource().shouldUseCloudModel)
    }

    @Test
    fun memoryBelowThreshold_isLow() {
        val r = resource(availableMemoryMb = 1023)
        assertTrue(r.isMemoryLow)
        assertTrue(r.shouldUseCloudModel)
    }

    @Test
    fun memoryExactlyAtThreshold_isNotLow() {
        val r = resource(availableMemoryMb = 1024)
        assertFalse(r.isMemoryLow)
    }

    @Test
    fun batteryBelow20_isLow() {
        val r = resource(batteryPercent = 19)
        assertTrue(r.isBatteryLow)
        assertTrue(r.shouldUseCloudModel)
    }

    @Test
    fun batteryExactly20_isNotLow() {
        assertFalse(resource(batteryPercent = 20).isBatteryLow)
    }

    @Test
    fun temperatureAtOrAbove45_isOverheated() {
        assertTrue(resource(batteryTemperature = 45f).isOverheated)
        assertTrue(resource(batteryTemperature = 50f).shouldUseCloudModel)
    }

    @Test
    fun temperatureBelow45_isNotOverheated() {
        assertFalse(resource(batteryTemperature = 44.9f).isOverheated)
    }

    @Test
    fun anySingleResourceShortage_shouldUseCloud() {
        assertTrue(resource(availableMemoryMb = 100).shouldUseCloudModel)
        assertTrue(resource(batteryPercent = 5).shouldUseCloudModel)
        assertTrue(resource(batteryTemperature = 70f).shouldUseCloudModel)
    }

    @Test
    fun cpuUsageIsReportedButNotDecisionInput() {
        val r = resource(cpuUsagePercent = 99f)
        assertFalse(r.shouldUseCloudModel)
    }
}