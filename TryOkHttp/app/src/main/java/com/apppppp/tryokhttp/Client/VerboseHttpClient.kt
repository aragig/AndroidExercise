package com.apppppp.tryokhttp.Client

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import android.os.Looper.getMainLooper




class VerboseHttpClient(_url:String, _body:String? = null):HTTPClient {

    override val url: String = _url
    override val body:String? = _body

    private fun isCurrent(): Boolean {
        return Thread.currentThread() == getMainLooper().thread
    }

    override fun request(): String? {
        var result: String? = null
        var conn: HttpURLConnection? = null
        try {
            Log.d("mopi", "isCurrent ${isCurrent()}")

            conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = if(body == null) "GET" else "POST"
            conn.connectTimeout = 2000
//            conn.readTimeout = 10000
//            conn.doOutput = false
            conn.connect()

            if(body != null) {
                val oStream = conn.outputStream
                oStream.write(body.toByteArray())
                oStream.flush()
                oStream.close()
            }

            if (conn.responseCode == 200) {
                val iStream = conn.inputStream
                result = iStream.bufferedReader().use { it.readText() }
            }
        } finally {
            conn?.disconnect()
        }
        return result
    }

}