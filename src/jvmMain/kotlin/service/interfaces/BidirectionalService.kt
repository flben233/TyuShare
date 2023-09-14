package service.interfaces

interface BidirectionalService {
    fun sendCommendAndStop()
    fun sendCommendAndStart()
    fun start()
    fun stop()
}