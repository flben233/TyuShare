package util

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

sealed class LoggerUtil {
    companion object Default: LoggerUtil()

    private val logFile = File("./tyu_share.log")

    init {
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

    fun logStackTrace(stackTrace: Array<StackTraceElement>) {
        val writer = BufferedWriter(FileWriter(logFile, true))
        for (traceElement in stackTrace) {
            writer.append("${DateUtil.getDateString()}:  at $traceElement \n")
        }
        writer.close()
    }
}