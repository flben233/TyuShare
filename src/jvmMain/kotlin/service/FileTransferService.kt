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


/**
 * 文件传输服务实现
 * @author ShirakawaTyu
 * @since 9/17/2023 5:16 PM
 * @version 1.0
 */
sealed class FileTransferService {
    companion object Default : FileTransferService()

    private val filePort = SERVICE_PORT + 3

    /**
     * 发送一个文件
     * @param filePath 文件路径，比如"/var/caddy.conf"
     * @author ShirakawaTyu
     */
    fun sendFile(filePath: String) {
        val file = File(filePath)
        CoroutineScope(Dispatchers.IO).launch {
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

    /**
     * 接收文件，保存到设置好的路径下
     * @param fileName 保存到的文件名
     * @param fileSize 文件大小
     * @author ShirakawaTyu
     */
    fun receiveFile(fileName: String, fileSize: Long) {
        val client = Socket(ConnectionService.getTargetIp(), filePort)
        CoroutineScope(Dispatchers.IO).launch {
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