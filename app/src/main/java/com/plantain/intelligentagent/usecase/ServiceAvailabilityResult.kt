package com.plantain.intelligentagent.usecase

data class ServiceAvailabilityResult(
    val isInstalled: Boolean,
    val isRunning: Boolean
) {
    val isAvailable: Boolean
        get() = isInstalled && isRunning
}