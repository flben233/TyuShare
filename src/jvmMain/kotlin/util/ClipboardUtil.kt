package util

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.FlavorListener
import java.awt.datatransfer.StringSelection


sealed class ClipboardUtil {
    companion object Default : ClipboardUtil()

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    private var clipboardListener: FlavorListener? = null
    private var clipboardHandler: () -> Unit = {}

    fun listen(listener: () -> Unit) {
        clipboardListener = FlavorListener { listener() }
        clipboardHandler = listener
        clipboard.addFlavorListener(clipboardListener)
    }

    fun getStr(): String? {
        var str: String? = null
        try {
            str = Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String
        } catch (e: Exception) {
            LoggerUtil.logStackTrace(e.stackTrace)
        }
        if (str != null) {
            setStr(str.trim())
        }
        return str
    }

    fun setStr(str: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(str.trim()), null)
    }
}

