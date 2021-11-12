package kr.server24.socket

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ExceptionListener
import io.netty.channel.ChannelHandlerContext
import org.apache.log4j.PropertyConfigurator
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.nio.file.Paths


class SslChatLauncher {

    companion object{
        @Throws(InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val log4jConfPath = "src/main/resources/log4j.properties"
            PropertyConfigurator.configure(log4jConfPath)
            val config = Configuration()
            config.hostname = "localhost"
            config.port = 8080
            config.keyStorePassword = "aS123123!"
            val path = Paths.get("src", "main", "kotlin", "kr", "server24", "server24-keystore.jks").toString()
            val kf = File(path)
//            val stream = SslChatLauncher::class.java.getResourceAsStream("/server24-keystore.jks")
            config.keyStore = FileInputStream(kf)
            config.exceptionListener = object : ExceptionListener {
                override fun onEventException(e: Exception?, args: MutableList<Any>?, client: SocketIOClient?) {
                    e?.printStackTrace()
                }

                override fun onDisconnectException(e: Exception?, client: SocketIOClient?) {
                    e?.printStackTrace()
                }

                override fun onConnectException(e: Exception?, client: SocketIOClient?) {
                    e?.printStackTrace()
                }

                override fun onPingException(e: Exception?, client: SocketIOClient?) {
                    e?.printStackTrace()
                }

                override fun exceptionCaught(ctx: ChannelHandlerContext?, e: Throwable?): Boolean {
                    e?.printStackTrace()
                    return true
                }
            }
            val server = SocketIOServer(config)
            server.addEventListener("chatevent", ChatObject::class.java) { client, data, ackRequest ->
                server.broadcastOperations.sendEvent("chatevent", data)
                println("data ${data}")
            }
            server.start()


        }
    }

}