package com.apppppp.tryokhttpasync

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.apppppp.tryokhttpasync.model.AsyncPost
import com.apppppp.tryokhttpasync.model.HttpClient
import com.apppppp.tryokhttpasync.model.HttpRequestCallback
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Callback
import java.io.IOException

class MainActivity : AppCompatActivity() {


//    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        request.setOnClickListener {

//            if(isLoading) {
//                return@setOnClickListener
//            }
//
//            isLoading = true

            textView.text = ""

            textView.text = ApiGenerator.fetchEchoApi()

        }

        cancel.setOnClickListener {
        }
    }





}


object ApiGenerator {

    var mRequestCounter = 0

    fun fetchEchoApi():String {

        val param = mapOf(
            "name" to "リクエスター(No.${ApiGenerator.mRequestCounter})",
            "message" to "こんにちは、世界",
            "response_type" to "string"
        )
        val httpClient = HttpClient()
        return httpClient.post("https://apppppp.com/post_echo.php", param)

    }


}


