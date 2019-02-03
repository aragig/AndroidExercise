package com.apppppp.retrofitsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.concurrent.thread



data class JoJo(val name: String = "", val stand: String = "")


interface JoJoService {
    /************************

    http://apppppp.com/jojo.json からJSONデータを取ってくる

    -------レスポンスの中身-------
    {
        "name":"Jyotaro",
        "stand":"The World"
    }
    -------おわり-----------

     *************************/

    @GET("jojo.json")
    fun fetchJoJo(): Call<JoJo>
}



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://apppppp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            /** ↑↑↑↑↑↑↑↑
             Gsonのファクトリーメソッド必須！

            これがないと次のようなエラーが出てハマる
            java.lang.IllegalArgumentException: Unable to create converter for class com.apppppp.retrofitsample.JoJo
            for method JoJoService.fetchJoJo
             **/
            .build()


        val handler = Handler()

        thread { // Retrofitはメインスレッドで処理できない
            try {
                val service:JoJoService = retrofit.create(JoJoService::class.java)
                val jojo = service.fetchJoJo().execute().body() ?: throw IllegalStateException("bodyがnullだよ！")

                handler.post(Runnable {
                    // メインスレッドで処理
                    val nameTextView = findViewById<TextView>(R.id.name)
                    val standTextView = findViewById<TextView>(R.id.stand)

                    nameTextView.text = jojo.name
                    standTextView.text = jojo.stand
                })


            } catch (e: Exception) {
                Log.d("mopi", "debug $e")
            }
        }

    }
}
