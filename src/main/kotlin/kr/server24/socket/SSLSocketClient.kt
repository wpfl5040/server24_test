package kr.server24.socket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URI
import java.nio.file.Paths
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */



internal class WebSocketChatClient(serverUri: URI?) : WebSocketClient(serverUri) {
    override fun onOpen(handshakedata: ServerHandshake) {
        println("Connected")
    }

    override fun onMessage(message: String) {
        println("got: $message")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println("Disconnected")
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
    }
}

class SSLSocketClient {
    /*
   * Keystore with certificate created like so (in JKS format):
   *
   *keytool -genkey -keyalg RSA -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
   */

    companion object{
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val chatclient = WebSocketChatClient(URI("wss://bot.server24.kr:8887"))

            // load up the key store
            val STORETYPE = "JKS"
            val KEYSTORE = Paths.get("src", "main", "kotlin", "kr", "server24", "server24-keystore.jks").toString()
            val STOREPASSWORD = "aS123123!"
            val KEYPASSWORD = "aS123123!"
            val ks = KeyStore.getInstance(STORETYPE)
            val kf = File(KEYSTORE)
            ks.load(FileInputStream(kf), STOREPASSWORD.toCharArray())
            val kmf = KeyManagerFactory.getInstance("SunX509")
            kmf.init(ks, KEYPASSWORD.toCharArray())
            val tmf = TrustManagerFactory.getInstance("SunX509")
            tmf.init(ks)
            var sslContext: SSLContext? = null
            sslContext = SSLContext.getInstance("TLS")
//            sslContext.init(kmf.keyManagers, tmf.trustManagers, null)
        sslContext.init( null, null, null );
            // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//            val factory = SSLSocketFactory.getDefault()
            val factory = sslContext.socketFactory // (SSLSocketFactory) SSLSocketFactory.getDefault();
            chatclient.setSocketFactory(factory)
            chatclient.connectBlocking()
            val reader = BufferedReader(InputStreamReader(System.`in`))
            while (true) {
                when (val line = reader.readLine()) {
                    "close" -> {
                        chatclient.closeBlocking()
                    }
                    "open" -> {
                        chatclient.reconnect()
                    }
                    else -> {
                        chatclient.send(line)
                    }
                }
            }
        }
    }

}