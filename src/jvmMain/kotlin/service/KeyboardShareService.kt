package service

import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import service.interfaces.BidirectionalService

sealed class KeyboardShareService: BidirectionalService {

    companion object Default : KeyboardShareService()

    override fun sendCommendAndStop() {
        TODO("Not yet implemented")
    }

    override fun sendCommendAndStart() {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    fun restart() {
        TODO("Not yet implemented")
    }

    fun sendMouse() {

    }

    fun sendKey() {

    }
}