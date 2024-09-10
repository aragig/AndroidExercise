package com.example.protowebsocket

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.NetworkInterface
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var server: MyWebSocketServer? = null
    private lateinit var messageTextView: TextView
    private var isWsServerStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        messageTextView = findViewById(R.id.messageTextView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val wsServerButton: Button = findViewById(R.id.wsServerButton)

        wsServerButton.setOnClickListener {
            if (!isWsServerStarted) {
                startWsServer()
                wsServerButton.text = "Stop"
                isWsServerStarted = true
                messageTextView.text = "メッセージの受信待ち。"
            } else {
                stopWsServer()
                wsServerButton.text = "Start"
                isWsServerStarted = false
                messageTextView.text = ""
            }
        }


    }


    // Activityが破棄される際にリソースを解放
    override fun onDestroy() {
        super.onDestroy()
        println("onDestory()が呼ばれた")

        stopWsServer()

    }

    private fun startWsServer() {
        // 非同期でIPアドレスを取得してWebSocketサーバーを起動
        CoroutineScope(Dispatchers.Main).launch {
            val ipAddress = getIPAddress()
            println("IP Address: $ipAddress")

            // WebSocketサーバーを起動
            server = MyWebSocketServer(InetSocketAddress(ipAddress, 8765))
            // WebSocketサーバーのコールバックを設定
            server?.setWebSocketCallback(object : MyWebSocketServer.WebSocketCallback {
                override fun onMessageReceived(message: String) {
                    // UIスレッドでTextViewを更新
                    runOnUiThread {
                        messageTextView.text = message
                    }
                }
            })
            println("Begin server... ws://${ipAddress}:8765")

            thread {
                // TODO 「Error: Address already in use」 だった場合にstopさせてからもう一度処理する
                server?.start()
            }
        }
    }

    private fun stopWsServer() {
        // サーバーが起動している場合、停止
        server?.stop()
        // コールバックの解放
        server?.setWebSocketCallback(null)
        println("WebSocket server stopped.")
    }

    // TODO serviceクラスへ移す
    private fun getIPAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (iface in interfaces) {
                print("iface.name: ")
                println(iface.name)
                // Wi-Fiインターフェースかどうか確認 (通常 "wlan0" がWi-Fiのインターフェース名)
                if (iface.name.equals("wlan2", ignoreCase = true) && iface.isUp) {
                    val addresses = iface.inetAddresses
                    for (addr in addresses) {
                        if (!addr.isLoopbackAddress && addr.hostAddress.indexOf(':') == -1) {
                            return addr.hostAddress ?: "127.0.0.1"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "127.0.0.1" // IPが取得できなかった場合のフォールバック
    }
}