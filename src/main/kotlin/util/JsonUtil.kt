package util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets


sealed class JsonUtil {

    companion object Default : JsonUtil()

    inline fun <reified T> parseJsonFile(filePath: String, default: T): T {
        try {
            val json = File(filePath)
            if (!json.exists()) {
                return default
            }
            val fileReader = FileReader(json, StandardCharsets.UTF_8)
            val t = Json.decodeFromString<T>(fileReader.readText())
            fileReader.close()
            return t
        } catch (_: Exception) {
            return default
        }
    }

    inline fun <reified T> toJsonFile(filePath: String, obj: T) {
        val json = File(filePath)
        val path = File(filePath.substring(0, filePath.lastIndexOf("\\")))
        if (!path.exists()) {
            path.mkdirs()
        }
        if (!json.exists()) {
            json.createNewFile()
        }
        val fileWriter = FileWriter(json, StandardCharsets.UTF_8)
        fileWriter.write(Json.encodeToString(obj))
        fileWriter.close()
    }
}
