package service.interfaces

/**
 * 描述一个需要双向确认的服务，比如音频传输和键鼠共享
 * @author ShirakawaTyu
 * @since 9/17/2023 5:07 PM
 * @version 1.0
 */
interface BidirectionalService {
    fun sendCommendAndStop()
    fun sendCommendAndStart()
    fun start()
    fun stop()
}