package com.apppppp.trycoroutinehttprequest

import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class HttpClient {

    private val mOkHttpClient = OkHttpClient()


    @Throws(IOException::class)
    fun getCall(url: String): Call {

        val request = Request.Builder()
            .url(url)
            .build()

        return mOkHttpClient.newCall(request)
    }

    @Throws(IOException::class)
    fun postCall(url: String, params:Map<String,String>): Call {

        val formBody = FormBody.Builder().apply {
            params.forEach {(key, value) ->
                add(key, value)
            }
        }.build()


        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return mOkHttpClient.newCall(request)
    }



    companion object {
        var INSTANCE:HttpClient? = null
        fun getInstance():HttpClient = INSTANCE ?: HttpClient().also {
            INSTANCE = it
        }
    }

}