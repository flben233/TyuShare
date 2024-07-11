package util

import component.tool.FileStreamProgress
import java.io.InputStream
import java.io.OutputStream

sealed class IoUtil {
    companion object Default : IoUtil()

    fun copy(inputStream: InputStream, outputStream: OutputStream, bufferSize: Int, progress: FileStreamProgress) {
        val buffer = ByteArray(bufferSize)
        var len: Int
        var progressSize = 0L
        progress.start()
        try {
            while (inputStream.read(buffer).also { len = it } != -1) {
                outputStream.write(buffer, 0, len)
                progressSize += len
                progress.progress(inputStream.available().toLong(), progressSize)
            }
        } finally {
            progress.finish()
        }
    }
}