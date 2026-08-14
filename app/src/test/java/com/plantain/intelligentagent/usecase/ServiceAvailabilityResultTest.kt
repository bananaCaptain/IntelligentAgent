package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 服务可用性测试
 * 服务可用的条件是：已安装且正在运行
 */
class ServiceAvailabilityResultTest {

    /**
     * 服务可用的条件是：已安装且正在运行
     */
    @Test
    fun available_onlyWhenInstalledAndRunning() {
        assertTrue(ServiceAvailabilityResult(isInstalled = true, isRunning = true).isAvailable)
        assertFalse(ServiceAvailabilityResult(isInstalled = true, isRunning = false).isAvailable)
        assertFalse(ServiceAvailabilityResult(isInstalled = false, isRunning = true).isAvailable)
        assertFalse(ServiceAvailabilityResult(isInstalled = false, isRunning = false).isAvailable)
    }
}