package com.apppppp.tryokhttp.Client

import java.net.HttpURLConnection
import java.net.URL

class VerboseHttpClient:HTTPClient {

    override fun request(url: String, body:String?): String? {
        var result: String? = null
        var conn: HttpURLConnection? = null
        try {
            conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = if(body == null) "GET" else "POST"
            conn.connectTimeout = 10000
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