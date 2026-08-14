package com.plantain.intelligentagent.usecase

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
/**
 *  用例检索设备资源信息，如内存、CPU使用率和电池状态。
 */
class DeviceResourceUseCase(private val context: Context) {

    suspend fun execute(): DeviceResourceResult = withContext(Dispatchers.IO) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        val totalMemoryMb = memInfo.totalMem / (1024 * 1024)
        val availableMemoryMb = memInfo.availMem / (1024 * 1024)
        val cpuUsage = readCpuUsage()
        val (batteryPercent, batteryTemp) = readBatteryState()

        DeviceResourceResult(
            availableMemoryMb = availableMemoryMb,
            totalMemoryMb = totalMemoryMb,
            cpuUsagePercent = cpuUsage,
            batteryPercent = batteryPercent,
            batteryTemperature = batteryTemp
        )
    }

    private fun readBatteryState(): Pair<Int, Float> {
        val intent = context.registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return Pair(-1, -1f)

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val percent = if (level >= 0 && scale > 0) level * 100 / scale else -1

        val temperatureTenths = intent.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE, -1
        )
        val tempCelsius = if (temperatureTenths >= 0) temperatureTenths / 10f else -1f

        return Pair(percent, tempCelsius)
    }

    private fun readCpuUsage(): Float {
        val (total1, idle1) = readCpuTimes() ?: return 0f
        Thread.sleep(CPU_SAMPLE_INTERVAL_MS)
        val (total2, idle2) = readCpuTimes() ?: return 0f

        val totalDelta = total2 - total1
        val idleDelta = idle2 - idle1
        if (totalDelta <= 0) return 0f

        return ((totalDelta - idleDelta).toFloat() / totalDelta) * 100f
    }

    // Returns (total, idle) jiffies from /proc/stat, or null if unreadable.
    private fun readCpuTimes(): Pair<Long, Long>? {
        return try {
            val line = File("/proc/stat").readLines().firstOrNull { it.startsWith("cpu ") } ?: return null
            val parts = line.split(Regex("\\s+")).drop(1)
            val times = parts.mapNotNull { it.toLongOrNull() }
            if (times.size < 4) return null
            val user = times.getOrElse(0) { 0L }
            val nice = times.getOrElse(1) { 0L }
            val system = times.getOrElse(2) { 0L }
            val idle = times.getOrElse(3) { 0L }
            Pair(user + nice + system + idle, idle)
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val CPU_SAMPLE_INTERVAL_MS = 200L
    }
}