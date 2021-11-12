package kr.server24.socket

import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.nio.ByteBuffer

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
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
class ChatServer : WebSocketServer {
    constructor(port: Int) : super(InetSocketAddress(port)) {}
    constructor(address: InetSocketAddress?) : super(address) {}
    constructor(port: Int, draft: Draft_6455) : super(InetSocketAddress(port), listOf<Draft>(draft)) {}

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        conn.send("Welcome to the server!") //This method sends a message to the new client
        broadcast(
            "new connection: " + handshake
                .resourceDescriptor
        ) //This method sends a message to all clients connected
        println(
            conn.remoteSocketAddress.address.hostAddress + " entered the room!"
        )
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        broadcast("$conn has left the room!")
        println("$conn has left the room!")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        broadcast(message)
        println("$conn: $message")
    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        broadcast(message.array())
        println("$conn: $message")
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    override fun onStart() {
        println("Server started!")
        connectionLostTimeout = 0
        connectionLostTimeout = 100
    }

    companion object {
        @Throws(InterruptedException::class, IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            var port = 8080 // 843 flash policy port
            try {
                port = args[0].toInt()
            } catch (ex: Exception) {
            }
            val s = ChatServer(port)
            s.start()
            println("asdad : ${s.address}")
            println("ChatServer started on port: " + s.port)
            val sysin = BufferedReader(InputStreamReader(System.`in`))
            while (true) {
                val `in` = sysin.readLine()
                s.broadcast(`in`)
                if (`in` == "exit") {
                    s.stop(1000)
                    break
                }
            }
        }
    }
}