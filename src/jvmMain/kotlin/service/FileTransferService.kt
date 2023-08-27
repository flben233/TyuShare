package service

import applicationSetting
import cn.hutool.core.io.IoUtil
import common.HttpCommend
import component.tool.FileStreamProgress
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.CommendUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets

sealed class FileTransferService {
    companion object Default : FileTransferService()

    private val filePort = SERVICE_PORT + 3

    fun sendFile(filePath: String) {
        val file = File(filePath)
        CoroutineScope(Dispatchers.Default).launch {
            val server = ServerSocket(filePort)
            val client = server.accept()
            val outputStream = client.getOutputStream()
            val fileIn = FileInputStream(filePath)
            IoUtil.copy(fileIn, outputStream, 4096, FileStreamProgress(file.length()))
            outputStream.close()
            client.close()
            fileIn.close()
        }
        val headers = HashMap<String, String>()
        headers["File-Name"] = Json.encodeToString(file.name.toByteArray(StandardCharsets.UTF_8))
        headers["File-Size"] = file.length().toString()
        CommendUtil.sendCommend(HttpCommend.SEND_FILE, ConnectionService.getTargetIp(), headers) {}
    }

    fun receiveFile(fileName: String, fileSize: Long) {
        val client = Socket(ConnectionService.getTargetIp(), filePort)
        CoroutineScope(Dispatchers.Default).launch {
            val path = File(applicationSetting.fileReceivePath.value)
            if (!path.exists()) {
                path.mkdirs()
            }
            val dest: OutputStream = FileOutputStream(
                applicationSetting.fileReceivePath.value + File.separator +
                        String(Json.decodeFromString(fileName) as ByteArray, StandardCharsets.UTF_8)
            )
            val inputStream = client.getInputStream()
            IoUtil.copy(inputStream, dest, 4096, FileStreamProgress(fileSize))
            inputStream.close()
            dest.close()
            client.close()
        }
    }
}