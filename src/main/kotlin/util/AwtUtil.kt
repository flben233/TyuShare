package util

import java.awt.Desktop

sealed class AwtUtil {
    companion object Default : AwtUtil()

    fun browse(url: String) {
        Desktop.getDesktop().browse(java.net.URI(url))
    }
}