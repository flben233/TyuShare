package service

import androidx.compose.ui.window.Notification
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
import tray
import util.CommendUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
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
            var server: ServerSocket? = null
            var client: Socket? = null
            var outputStream: OutputStream? = null
            var fileIn: FileInputStream? = null
            try {
                server = ServerSocket(filePort)
                client = server.accept()
                outputStream = client.getOutputStream()
                fileIn = FileInputStream(filePath)
                IoUtil.copy(fileIn, outputStream, 4096, FileStreamProgress(file.length()))
            } catch (e: Exception) {
                tray.sendNotification(Notification("文件传输", "文件发送失败"))
                return@launch
            } finally {
                outputStream?.close()
                client?.close()
                fileIn?.close()
                server?.close()
            }
            tray.sendNotification(Notification("文件传输", "文件${file.name}发送完成"))
        }
        val headers = HashMap<String, String>()
        headers["File-Name"] = Json.encodeToString(file.name.toByteArray(StandardCharsets.UTF_8))
        headers["File-Size"] = file.length().toString()
        CommendUtil.sendCommend(HttpCommend.SEND_FILE, headers) {}
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
            var dest: OutputStream? = null
            var inputStream: InputStream? = null
            var file = File(applicationSetting.fileReceivePath.value + File.separator +
                    String(Json.decodeFromString(fileName) as ByteArray, StandardCharsets.UTF_8))
            file = renameOnExist(file)
            try {
                dest = FileOutputStream(file)
                inputStream = client.getInputStream()
                IoUtil.copy(inputStream, dest, 4096, FileStreamProgress(fileSize))
            } catch (e: Exception) {
                tray.sendNotification(Notification("文件传输", "文件接收失败"))
                file.delete()
                return@launch
            } finally {
                dest?.close()
                inputStream?.close()
                client.close()
            }
            tray.sendNotification(Notification("文件传输", "文件已保存至${applicationSetting.fileReceivePath.value}"))
        }
    }

    private fun renameOnExist(file: File): File {
        var count = 0
        var newFile = file
        while (newFile.exists()) {
            newFile = File(file.parent, "${file.nameWithoutExtension}(${++count}).${file.extension}")
        }
        return newFile
    }
}