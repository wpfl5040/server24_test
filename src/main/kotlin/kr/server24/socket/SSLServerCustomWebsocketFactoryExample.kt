package kr.server24.socket

import org.java_websocket.server.CustomSSLWebSocketServerFactory
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import java.security.KeyStore
import java.util.*
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
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



/**
 * Example for using the CustomSSLWebSocketServerFactory to allow just specific cipher suites
 */
object SSLServerCustomWebsocketFactoryExample {
    /*
   * Keystore with certificate created like so (in JKS format):
   *
   *keytool -genkey -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
   */
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val chatserver = ChatServer(
            8887
        ) // Firefox does allow multible ssl connection only via port 443 //tested on FF16

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
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(kmf.keyManagers, tmf.trustManagers, null)

        //Lets remove some ciphers and protocols
        val engine = sslContext.createSSLEngine()
        val ciphers: MutableList<String> = ArrayList(Arrays.asList(*engine.enabledCipherSuites))
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256")
        val protocols: MutableList<String> = ArrayList(Arrays.asList(*engine.enabledProtocols))
        protocols.remove("SSLv3")
        val factory = CustomSSLWebSocketServerFactory(
            sslContext, protocols.toTypedArray(), ciphers.toTypedArray()
        )

        // Different example just using specific ciphers and protocols
        /*
        String[] enabledProtocols = {"TLSv1.2"};
		String[] enabledCipherSuites = {"TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA"};
        CustomSSLWebSocketServerFactory factory = new CustomSSLWebSocketServerFactory(sslContext, enabledProtocols,enabledCipherSuites);
        */
        chatserver.setWebSocketFactory(factory)
        chatserver.start()
    }
}