package com.apppppp.trycoroutinehttprequest

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_sub.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import okhttp3.Call
import java.io.IOException

class SubActivity : ScopedAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        setup()
    }



    fun setup() {

        var message = ""
        launch {
            // メインスレッドで実行される
            var counter = 0
            while (true) {
                textView.text = "${++counter} $message"
                delay(100) // ブロッキングされない
            }
        }

        var requestCount = 0

        suspend fun fetchIOData():String = withContext(Dispatchers.IO) {
            Thread.sleep(3000)
            "リクエスト No.${++requestCount}"
        }

        button.onClick {
            message = fetchIOData()
        }


        fetch.onClick {
            message = ""
            try {
                message = JoJoApiGenetator.download()
            } catch (e:CancellationException) {
                println("コルーチンキャンセル！ $e")
            } catch (e:IOException) {
                println("ネットワークエラー！ $e")
            }
        }

        finish.onClick {
            finish()
        }
    }


    fun View.onClick(action: suspend (View) -> Unit) {
        val eventActor = GlobalScope.actor<View>(Dispatchers.Main, capacity = Channel.CONFLATED) {
            for (event in channel) action(event)
        }
        setOnClickListener {
            eventActor.offer(it)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EchoApiGenerator.cancelCall()
        JoJoApiGenetator.cancelCall()
        println("SubActivityのonDestroyが呼ばれた")
    }
}













const val JOJO_API = "https://apppppp.com/jojo.json"
const val POST_ECHO_API = "https://apppppp.com/post_echo.php"


object JoJoApiGenetator {
    var mCounter = 0
    var mCall:Call? = null


    @Throws(IOException::class)
    suspend fun download(): String = withContext(Dispatchers.IO) {

        if(mCall != null) {
            cancelCall()
        }
        mCall = HttpClient.getInstance().getCall(JOJO_API)

        mCall!!.execute().use { response ->

            if (!response.isSuccessful) throw IOException("レスポンスに失敗！ $response")

            "レスポンスNo.(${++mCounter})" + response.body()!!.string()
        }


    }

    fun cancelCall() {
        mCall?.cancel()
        mCall = null
    }

}

object EchoApiGenerator {
    var mCounter = 0
    var mCall:Call? = null
    var mCoroutine:CoroutineScope? = null



    @Throws(IOException::class)
    suspend fun download(): String = withContext(Dispatchers.IO) {

        mCoroutine = this

        val param = mapOf(
            "name" to "リクエスター",
            "message" to "こんにちは、世界",
            "response_type" to "string"
        )

        if(mCall != null) {
            cancelCall()
        }
        mCall = HttpClient.getInstance().postCall(POST_ECHO_API, param)

        mCall!!.execute().use { response ->

            if (!response.isSuccessful) throw IOException("レスポンスに失敗！ $response")
            println("ここを通った！")

            "レスポンスNo.(${++mCounter})" + response.body()!!.string()
        }


    }

    fun cancelCall() {
        mCall?.cancel()
        mCall = null
//        mCoroutine?.cancel()
    }

}


