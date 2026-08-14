package com.plantain.intelligentagent.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceAvailabilityResultTest {

    @Test
    fun available_onlyWhenInstalledAndRunning() {
        assertTrue(ServiceAvailabilityResult(isInstalled = true, isRunning = true).isAvailable)
        assertFalse(ServiceAvailabilityResult(isInstalled = true, isRunning = false).isAvailable)
        assertFalse(ServiceAvailabilityResult(isInstalled = false, isRunning = true).isAvailable)
        assertFalse(ServiceAvailabilityResult(isInstalled = false, isRunning = false).isAvailable)
    }
}