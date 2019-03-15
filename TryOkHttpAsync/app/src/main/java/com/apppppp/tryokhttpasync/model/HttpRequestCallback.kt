package com.apppppp.tryokhttpasync.model
import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

abstract class HttpRequestCallback:Callback {

    var mResult:String? = null

    abstract fun onSuccess(responseBody:String)

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {

        response.body()!!.use { responseBody ->


            if (!response.isSuccessful) throw IOException("Unexpected code $response")


            val responseBody = responseBody.string() // toStringを使わないこと！
//            val responseHeader = response.headers()
            this.mResult = responseBody

//            runOnUiThread {
//                try {
//                    onSuccess(responseBody)
//                } catch (e:IOException) {
//                    onFailure(call, e)
//                }
//            }
            try {
                onSuccess(responseBody)
            } catch (e:IOException) {
                onFailure(call, e)
            }

        }
    }

//    private fun runOnUiThread( task:()->Unit) {
//        Handler(Looper.getMainLooper()).post {
//            task()
//        }
//
//    }

    public fun getResult():String? = mResult


}