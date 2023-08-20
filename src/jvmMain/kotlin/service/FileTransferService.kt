package service

import applicationSetting
import cn.hutool.core.io.IoUtil
import common.HttpCommend
import component.tool.FileStreamProgress
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import util.CommendUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

sealed class FileTransferService {
    companion object Default : FileTransferService()

    private val filePort = SERVICE_PORT + 3

    fun sendFile(filePath: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val server = ServerSocket(filePort)
            val client = server.accept()
            val outputStream = client.getOutputStream()
            val fileIn = FileInputStream(filePath)
            IoUtil.copy(fileIn, outputStream, 4096, FileStreamProgress())
            outputStream.close()
            client.close()
            fileIn.close()
        }
        val headers = HashMap<String, String>()
        headers["File-Name"] = File(filePath).name
        CommendUtil.sendCommend(HttpCommend.SEND_FILE, ConnectionService.getTargetIp(), headers) {}
    }

    fun receiveFile(fileName: String) {
        val client = Socket(ConnectionService.getTargetIp(), filePort)
        CoroutineScope(Dispatchers.Default).launch {
            val path = File(applicationSetting.fileReceivePath)
            if (!path.exists()) {
                path.mkdirs()
            }
            val dest: OutputStream = FileOutputStream(applicationSetting.fileReceivePath + File.separator + fileName)
            val inputStream = client.getInputStream()
            IoUtil.copy(inputStream, dest, 4096, FileStreamProgress())
            inputStream.close()
            dest.close()
            client.close()
        }
    }
}