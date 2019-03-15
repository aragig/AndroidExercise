package com.apppppp.tryokhttpasync.model

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class HttpClient {

    var mClient = OkHttpClient()

    @Throws(IOException::class)
    fun post(url: String, params:Map<String,String>): String {

        val formBody = FormBody.Builder().apply {
            params.forEach {(key, value) ->
                add(key, value)
            }
        }.build()


        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        mClient.newCall(request).execute().use { response -> return response.body()!!.string() }
    }

}

