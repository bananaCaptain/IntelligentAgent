package com.plantain.intelligentagent.usecase

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用例检查智能服务应用的可用性，包括是否安装和是否正在运行。
 */
class ServiceAvailabilityUseCase(private val context: Context) {

    suspend fun execute(): ServiceAvailabilityResult = withContext(Dispatchers.IO) {
        ServiceAvailabilityResult(
            isInstalled = isServiceAppInstalled(),
            isRunning = isServiceAppRunning()
        )
    }

    private fun isServiceAppInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(SERVICE_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isServiceAppRunning(): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // PkgList requires only visibility; on API 29+ without visible apps this may be
        // restricted, fall back to checking an AIDL bound state elsewhere if needed.
        return try {
            am.runningAppProcesses
                ?.any { it.pkgList.contains(SERVICE_PACKAGE) }
                ?: false
        } catch (e: SecurityException) {
            false
        }
    }

    private companion object {
        const val SERVICE_PACKAGE = "com.plantain.intelligentservice"
    }
}