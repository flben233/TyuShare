package util

import oshi.SystemInfo

sealed class HardwareUtil {
    companion object Default : HardwareUtil()
    private val hardware = SystemInfo().hardware
    private val processor = hardware.processor
    private val memory = hardware.memory
    private val ips = ArrayList<String>()
    init {
        hardware.networkIFs.forEach {
            ips.addAll(it.iPv4addr)
        }
    }

    fun getCpuLoadFloat(): Float {
        return processor.getSystemCpuLoad(1000).toFloat()
    }

    fun getCpuLoad(): String {
        return String.format("%.2f", getCpuLoadFloat() * 100) + "%"
    }

    fun getCpuThreadCount(): String {
        return "${processor.logicalProcessorCount}"
    }

    fun getMemoryUsageFloat(): Float {
        return 1 - memory.available.toFloat() / memory.total
    }

    fun getMemoryUsage(): String {
        val totalGByte = String.format("%.2f", memory.total / (1024 * 1024 * 1024.0))
        val usedGByte = String.format("%.2f", (memory.total - memory.available) / (1024 * 1024 * 1024.0))
        return "${String.format("%.2f", getMemoryUsageFloat() * 100)}% $usedGByte/$totalGByte GB "
    }

    fun isCharging(): String {
        if (hardware.powerSources[0].isCharging) {
            return "正在充电"
        }
        return "未在充电"
    }

    fun getNetworkAddress(): List<String> {
        return ips
    }

}