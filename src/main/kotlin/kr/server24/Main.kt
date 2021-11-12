package kr.server24

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import io.netty.channel.ChannelHandlerContext
import kr.server24.socket.ChatObject
import org.apache.log4j.PropertyConfigurator
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.nio.file.Paths


fun main(args: Array<String>) {
//    SSLSocketServer().startServer()
    val log4jConfPath = "/root/bot/log4j.properties"
    PropertyConfigurator.configure(log4jConfPath)


    val config = Configuration()
    config.hostname = "158.247.227.183"
    config.port = 8080
//    config.origin = "https://bot.server24.kr:8080"
//    config.keyStorePassword = "aS123123!"
//    val path = Paths.get("/root", "bot", "server24-keystore.jks").toString()
//    val kf = File(path)
//    config.keyStore = FileInputStream(kf)
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
    println("start")

}