package com.apppppp.tryokhttp.Client

import android.util.Log
import okhttp3.*
import okhttp3.OkHttpClient

class OkHttpClient:HTTPClient {

    /**********************************
     * http://square.github.io/okhttp/
     **********************************/

    var client = OkHttpClient()

    override fun request(url: String, body:String?): String? {
        val builder = Request.Builder()
        val request = if(body != null) {
            Log.d("mopi", body)

            val requestBody = buildMultipartBody(body)

            builder.url(url).post(requestBody).build()
        } else {
            builder.url(url).build()
        }

        client.newCall(request).execute().use { response -> return response.body()?.string() }
    }


    fun buildMultipartBody(body:String):MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for ((k, v) in body.toParams()) {
            builder.addFormDataPart(k, v)
        }
        return builder.build()
    }

    fun String.toParams():List<List<String>> = this
        .split(Regex("&"))
        .map { it.split(Regex("=")) }
}