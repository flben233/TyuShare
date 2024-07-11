package util

import java.util.*


sealed class DateUtil {
    companion object Default : DateUtil()

    fun getDateString(): String {
        return Date().toString()
    }
}
