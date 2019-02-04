package com.apppppp.tryokhttp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.apppppp.tryokhttp.Client.HTTPTask
import com.apppppp.tryokhttp.Client.OkHttpClient
import com.apppppp.tryokhttp.Client.VerboseHttpClient

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doGetVerboseClient()
        doPostVerboseClient()
        doGetOkHttpClient()
        doPostOkHttpClient()


    }

    val requestURL = "https://apppppp.com/echo.php"

    fun doGetOkHttpClient() {
        val client = OkHttpClient()
        val task = HTTPTask(client)
        val result = task.execute(requestURL).get() ?: "結果がNull!"
        val resultText = findViewById<TextView>(R.id.result3)
        resultText.text = result
    }

    fun doPostOkHttpClient() {
        val client = OkHttpClient()
        val task = HTTPTask(client)
        val result = task.execute(requestURL, "body=やっほー!&name=OkHttpClient").get() ?: "結果がNull!"
        val resultText = findViewById<TextView>(R.id.result4)
        resultText.text = result
    }
    fun doGetVerboseClient() {
        val client = VerboseHttpClient()
        val task = HTTPTask(client)
        val result = task.execute(requestURL).get() ?: "結果がNull!"
        val resultText = findViewById<TextView>(R.id.result1)
        resultText.text = result
    }

    fun doPostVerboseClient() {
        val client = VerboseHttpClient()
        val task = HTTPTask(client)
        val result = task.execute(requestURL, "body=やっほー!&name=VerboseHttpClient").get() ?: "結果がNull!"
        val resultText = findViewById<TextView>(R.id.result2)
        resultText.text = result
    }



}
