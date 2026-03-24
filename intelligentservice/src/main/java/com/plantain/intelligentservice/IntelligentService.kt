package com.plantain.intelligentservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class IntelligentService : Service() {

    private val binder = object : IIntelligentService.Stub() {
        override fun getVerificationString(): String {
            Log.d("IntelligentService", "getVerificationString called")
            return "Hello from IntelligentService via AIDL!"
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("IntelligentService", "onBind called")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("IntelligentService", "onUnbind called")
        return super.onUnbind(intent)
    }
}
