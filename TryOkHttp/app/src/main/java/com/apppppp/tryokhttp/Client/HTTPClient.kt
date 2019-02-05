package com.apppppp.tryokhttp.Client

interface HTTPClient {
    val url:String
    val body:String?
    fun request(): String?
}