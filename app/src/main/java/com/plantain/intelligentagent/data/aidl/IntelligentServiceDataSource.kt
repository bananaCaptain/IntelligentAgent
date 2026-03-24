package com.plantain.intelligentagent.data.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.plantain.intelligentservice.IIntelligentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class IntelligentServiceDataSource {

    private var intelligentService: IIntelligentService? = null

    private val _isBound = MutableStateFlow(false)
    val isBound: StateFlow<Boolean> = _isBound.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            intelligentService = IIntelligentService.Stub.asInterface(service)
            _isBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            intelligentService = null
            _isBound.value = false
        }
    }

    fun bind(context: Context): Boolean {
        if (_isBound.value) return true

        val intent = Intent().apply {
            component = ComponentName(
                "com.plantain.intelligentservice",
                "com.plantain.intelligentservice.IntelligentService"
            )
        }

        val bindResult = context.applicationContext.bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        if (!bindResult) {
            _isBound.value = false
        }
        return bindResult
    }

    fun unbind(context: Context) {
        if (!_isBound.value) return
        runCatching {
            context.applicationContext.unbindService(serviceConnection)
        }
        intelligentService = null
        _isBound.value = false
    }

    suspend fun getVerificationString(): String = withContext(Dispatchers.IO) {
        val service = intelligentService ?: error("Service not bound")
        service.getVerificationString()
    }

    suspend fun getLlamaSystemInfo(): String = withContext(Dispatchers.IO) {
        val service = intelligentService ?: error("Service not bound")
        service.getLlamaSystemInfo()
    }

    suspend fun loadLlamaModel(modelPath: String, nCtx: Int): Int = withContext(Dispatchers.IO) {
        val service = intelligentService ?: error("Service not bound")
        service.loadLlamaModel(modelPath, nCtx)
    }

    suspend fun chatWithLlama(prompt: String): String = withContext(Dispatchers.IO) {
        val service = intelligentService ?: error("Service not bound")
        service.chatWithLlama(prompt)
    }
}
