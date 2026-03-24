package com.plantain.intelligentservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.plantain.llamakotlin.LlamaKotlin

class IntelligentService : Service() {

    private val llamaKotlin by lazy { LlamaKotlin() }

    private val binder = object : IIntelligentService.Stub() {
        override fun getVerificationString(): String {
            Log.d("IntelligentService", "getVerificationString called")
            return "Hello from IntelligentService via AIDL!"
        }

        override fun getLlamaSystemInfo(): String {
            return runCatching {
                llamaKotlin.systemInfo()
            }.getOrElse { error ->
                "getLlamaSystemInfo failed: ${error.message}"
            }
        }

        override fun loadLlamaModel(modelPath: String?, nCtx: Int): Int {
            if (modelPath.isNullOrBlank()) {
                return 2
            }
            return runCatching {
                val loadResult = llamaKotlin.loadModel(modelPath)
                if (loadResult != 0) {
                    return@runCatching loadResult
                }
                llamaKotlin.prepareContext(nCtx)
            }.getOrElse {
                3
            }
        }

        override fun chatWithLlama(prompt: String?): String {
            if (prompt.isNullOrBlank()) {
                return ""
            }
            return runCatching {
                llamaKotlin.chat(prompt)
            }.getOrElse { error ->
                "chatWithLlama failed: ${error.message}"
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        runCatching { llamaKotlin.initBackend() }
            .onFailure { error -> Log.e("IntelligentService", "initBackend failed", error) }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("IntelligentService", "onBind called")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("IntelligentService", "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        runCatching {
            llamaKotlin.release()
            llamaKotlin.shutdown()
        }.onFailure { error ->
            Log.e("IntelligentService", "release/shutdown failed", error)
        }
        super.onDestroy()
    }
}
