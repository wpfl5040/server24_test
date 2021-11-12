package kr.server24.socket

import org.java_websocket.server.DefaultSSLWebSocketServerFactory
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class SSLSocketServer {

    // load up the key store
    private val STORETYPE = "JKS"
    ///root/bot/server24-keystore.jks
    private val KEYSTORE = Paths.get("/root", "bot", "server24-keystore.jks").toString()
//    private val KEYSTORE = Paths.get("src", "main", "kotlin", "kr", "server24", "server24-keystore.jks").toString()
    private val STOREPASSWORD = "aS123123!"
    private val KEYPASSWORD = "aS123123!"

    fun startServer(){
        val chatserver = ChatServer(
            8887
        ) // Firefox does allow multible ssl connection only via port 443 //tested on FF16

        val ks = KeyStore.getInstance(STORETYPE)
        val kf = File(KEYSTORE)
        ks.load(FileInputStream(kf), STOREPASSWORD.toCharArray())

        val kmf = KeyManagerFactory.getInstance("SunX509")
        kmf.init(ks, KEYPASSWORD.toCharArray())
        val tmf = TrustManagerFactory.getInstance("SunX509")
        tmf.init(ks)

        var sslContext: SSLContext? = null
        sslContext = SSLContext.getInstance("TLS")
        sslContext.init(kmf.keyManagers, tmf.trustManagers, null)

        chatserver.setWebSocketFactory(DefaultSSLWebSocketServerFactory(sslContext))
        chatserver.start()
    }
}