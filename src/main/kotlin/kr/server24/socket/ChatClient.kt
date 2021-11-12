package kr.server24.socket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.URI
import java.net.URISyntaxException
import javax.swing.*

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



class ChatClient(defaultlocation: String?) : JFrame("WebSocket Chat Client"), ActionListener {
    private val uriField: JTextField
    private val connect: JButton
    private val close: JButton
    private val ta: JTextArea
    private val chatField: JTextField
    private val draft: JComboBox<*>
    private var cc: WebSocketClient? = null

    override fun actionPerformed(e: ActionEvent) {
        if (e.source === chatField) {
            if (cc != null) {
                cc!!.send(chatField.text)
                chatField.text = ""
                chatField.requestFocus()
            }
        } else if (e.source === connect) {
            try {
                // cc = new ChatClient(new URI(uriField.getText()), area, ( Draft ) draft.getSelectedItem() );
                cc = object : WebSocketClient(URI(uriField.text), draft.selectedItem as Draft) {
                    override fun onMessage(message: String) {
                        ta.append("got: $message\n")
                        ta.caretPosition = ta.document.length
                    }

                    override fun onOpen(handshake: ServerHandshake) {
                        ta.append(
                            """
                                You are connected to ChatServer: ${getURI()}
                                
                                """.trimIndent()
                        )
                        ta.caretPosition = ta.document.length
                    }

                    override fun onClose(code: Int, reason: String, remote: Boolean) {
                        ta.append(
                            """You have been disconnected from: ${getURI()}; Code: $code $reason"""
                        )
                        ta.caretPosition = ta.document.length
                        connect.isEnabled = true
                        uriField.isEditable = true
//                        draft.setEditable(true)
                        close.isEnabled = false
                    }

                    override fun onError(ex: Exception) {
                        ta.append("Exception occurred ...\n$ex\n")
                        ta.caretPosition = ta.document.length
                        ex.printStackTrace()
                        connect.isEnabled = true
                        uriField.isEditable = true
//                        draft.setEditable(true)
                        close.isEnabled = false
                    }
                }
                close.isEnabled = true
                connect.isEnabled = false
                uriField.isEditable = false
                draft.isEditable = false
                cc?.connect()
            } catch (ex: URISyntaxException) {
                ta.append(
                    """${uriField.text} is not a valid WebSocket URI
"""
                )
            }
        } else if (e.source === close) {
            cc!!.close()
        }
    }

    companion object {
        private const val serialVersionUID = -6056260699202978657L
        @JvmStatic
        fun main(args: Array<String>) {
            val location: String
            if (args.size != 0) {
                location = args[0]
                println("Default server url specified: \'$location\'")
            } else {
                location = "ws://bot.server24.kr:8887"
                println("Default server url not specified: defaulting to \'$location\'")
            }
            ChatClient(location)
        }
    }

    init {
        val c = contentPane
        val layout = GridLayout()
        layout.columns = 1
        layout.rows = 6
        c.layout = layout
        val drafts = arrayOf<Draft?>(Draft_6455())
        draft = JComboBox<Any?>(drafts)
        c.add(draft)
        uriField = JTextField()
        uriField.text = defaultlocation
        c.add(uriField)
        connect = JButton("Connect")
        connect.addActionListener(this)
        c.add(connect)
        close = JButton("Close")
        close.addActionListener(this)
        close.isEnabled = false
        c.add(close)
        val scroll = JScrollPane()
        ta = JTextArea()
        scroll.setViewportView(ta)
        c.add(scroll)
        chatField = JTextField()
        chatField.text = ""
        chatField.addActionListener(this)
        c.add(chatField)
        val d = Dimension(300, 400)
        preferredSize = d
        size = d
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                if (cc != null) {
                    cc!!.close()
                }
                dispose()
            }
        })
        setLocationRelativeTo(null)
        isVisible = true
    }
}