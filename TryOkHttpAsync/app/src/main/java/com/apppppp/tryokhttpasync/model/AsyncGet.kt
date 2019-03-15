package com.apppppp.tryokhttpasync.model


import okhttp3.*
import java.io.IOException

class AsyncGet {

    private val client = OkHttpClient()

    @Throws(Exception::class)
    fun doSomething(callback:(String?) -> Unit) {

        val request = Request.Builder()
            .url("https://apppppp.com/jojo.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                response.body()!!.use { responseBody ->

                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val responseHeaders = response.headers()
                    var i = 0
                    val size = responseHeaders.size()
                    while (i < size) {
                        println(responseHeaders.name(i) + ": " + responseHeaders.value(i))
                        i++
                    }

                    callback(responseBody.string())

///                    println(responseBody.string())
                }
            }
        })
    }



}