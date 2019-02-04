package com.apppppp.tryokhttp.Client

interface HTTPClient {
    fun request(url: String, body:String? = null): String?
}