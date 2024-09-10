package com.example.protowebsocket

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress


class MyWebSocketServer(address: InetSocketAddress) : WebSocketServer(address) {

    // コールバックインターフェースの定義
    interface WebSocketCallback {
        fun onMessageReceived(message: String)
    }

    // コールバックのインスタンス
    private var callback: WebSocketCallback? = null

    // コールバックを設定するメソッド
    fun setWebSocketCallback(callback: WebSocketCallback?) {
        this.callback = callback
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("New connection: ${conn.remoteSocketAddress}")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        println("Connection closed: ${conn.remoteSocketAddress}")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        println("Received: $message")
        // コールバックでActivityにメッセージを通知
        callback?.onMessageReceived(message)
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        println("Error: ${ex.message}")
    }

    override fun onStart() {
        println("WebSocket server started on port: $port")
    }
}
