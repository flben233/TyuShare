package util

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.FlavorEvent
import java.awt.datatransfer.FlavorListener
import java.awt.datatransfer.StringSelection


sealed class ClipboardUtil {
    companion object Default : ClipboardUtil()

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    fun listen(listener: () -> Unit) {
        clipboard.addFlavorListener(ClipboardListener(listener))
    }

    fun getStr(): String? {
        val str: String?
        try {
            clipboard.getContents(null)
            str = clipboard.getData(DataFlavor.stringFlavor) as String
        } catch (e: Exception) {
            LoggerUtil.logStackTrace(e.stackTrace)
            return null
        }
        return str
    }

    fun setStr(str: String) {
        clipboard.setContents(StringSelection(str), null)
    }

    class ClipboardListener(val listener: () -> Unit): FlavorListener {
        private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        override fun flavorsChanged(e: FlavorEvent?) {
            listener()
            clipboard.removeFlavorListener(this)
            clipboard.addFlavorListener(this)
        }
    }
}