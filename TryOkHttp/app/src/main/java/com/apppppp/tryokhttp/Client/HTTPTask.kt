package com.apppppp.tryokhttp.Client

import android.os.AsyncTask



class HTTPTask(private val client:HTTPClient) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String? { // AsyncTaskの抽象メソッド
        val url = params[0] ?: throw IllegalStateException("Not found url string! usage: HTTPTask().execute(url:String)")

        val body = if(params.size > 1) params[1] else null
        return client.request(url, body)
    }
}