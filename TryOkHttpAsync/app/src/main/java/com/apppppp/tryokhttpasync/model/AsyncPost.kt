package com.apppppp.tryokhttpasync.model

import okhttp3.*


class AsyncPost {

    var mCall:Call? = null

    @Throws(Exception::class)
    fun fetch(requestUrl:String, params:Map<String,String>, callback:Callback) {

        val formBody = FormBody.Builder().apply {
            params.forEach {(key, value) ->
                add(key, value)
            }
        }.build()



        val request = Request.Builder()
            .url(requestUrl)
            .post(formBody)
            .build()


        val client = OkHttpClient()
        mCall = client.newCall(request)
        mCall?.enqueue(callback)
    }


    fun cancell() {
        mCall?.cancel()
    }


}

