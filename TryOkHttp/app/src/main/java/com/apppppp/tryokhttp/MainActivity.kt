package com.apppppp.tryokhttp

import android.os.AsyncTask
import android.os.AsyncTask.execute
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.apppppp.tryokhttp.Client.*

class MainActivity : AppCompatActivity(), HTTPTask.Listener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button1 = findViewById<Button>(R.id.client1)
        button1.setOnClickListener {
            doOkHttpClient()
        }
        val button2 = findViewById<Button>(R.id.client2)
        button2.setOnClickListener {
            doPostOkHttpClient()
        }
        val button3 = findViewById<Button>(R.id.client3)
        button3.setOnClickListener {
            doVerboseClient()
        }
        val button4 = findViewById<Button>(R.id.client4)
        button4.setOnClickListener {
            doPostVerboseClient()
        }

    }



    val requestURL = "https://apppppp.com/echo.php"

    fun doOkHttpClient() {
        val client = OkHttpClient(requestURL)
        doTask(client)
    }
    fun doPostOkHttpClient() {
        val client = OkHttpClient(requestURL, "body=やっほー!&name=OkHttpClient")
        doTask(client)
    }
    fun doVerboseClient() {
        val client = VerboseHttpClient(requestURL)
        doTask(client)
    }
    fun doPostVerboseClient() {
        val client = VerboseHttpClient(requestURL, "body=やっほー!&name=VerboseHttpClient")
        doTask(client)
    }

    fun doTask(client: HTTPClient) {
        val task = HTTPTask(this, client)
        task.execute()
    }


    override fun didFinishedHTTPTask(result:String?) {
        val resultText = findViewById<TextView>(R.id.result)
        resultText.text = result ?: "結果がNullだよ！"
    }



}
