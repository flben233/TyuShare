package util

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.UnsupportedFlavorException

/**
 * 剪贴板工具类
 * @author ShirakawaTyu
 * @since 9/17/2023 5:25 PM
 * @version 1.0
 */
sealed class ClipboardUtil {
    companion object Default : ClipboardUtil()

    fun getStr(): String {
        var str = ""
        try {
            str = Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String
        } catch (_: UnsupportedFlavorException) {

        } catch (e: Exception) {
            LoggerUtil.logStackTrace(e)
        }
        return str
    }

    fun setStr(str: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(str), null)
    }
}

