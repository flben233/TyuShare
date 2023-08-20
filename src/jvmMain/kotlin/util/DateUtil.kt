package util

import java.util.Date


sealed class DateUtil {
    companion object Default : DateUtil()

    fun getDateString(): String {
        return Date().toString()
    }
}
