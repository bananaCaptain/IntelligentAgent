package com.plantain.intelligentagent.usecase

enum class NetworkType {
    WIFI,
    CELLULAR_5G,
    CELLULAR_4G,
    CELLULAR_3G,
    CELLULAR_2G,
    ETHERNET,
    NONE
}

data class NetworkStateResult(
    val type: NetworkType,
    val isConnected: Boolean,
    val isOnline: Boolean
) {
    val isStrongNetwork: Boolean
        get() = when (type) {
            NetworkType.WIFI,
            NetworkType.CELLULAR_5G,
            NetworkType.ETHERNET -> true
            else -> false
        }

    val isOfflineOrWeakNetwork: Boolean
        get() = !isOnline || type == NetworkType.CELLULAR_2G || type == NetworkType.CELLULAR_3G
}