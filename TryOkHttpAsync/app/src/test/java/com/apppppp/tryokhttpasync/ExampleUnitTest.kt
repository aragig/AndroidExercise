package com.apppppp.tryokhttpasync

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
import org.junit.Test
import org.mockito.Mockito.*
import android.os.Handler
import android.os.Looper
import com.apppppp.tryokhttpasync.model.HttpClient
import com.apppppp.tryokhttpasync.model.HttpRequestCallback
import okhttp3.*
import org.mockito.Mockito.`when`
import java.io.IOException
import java.lang.Exception


class NetworkServiceUnitTest {



    class Fuga {
        fun saySomething(index:Int):String {
            return when(index) {
                0 -> "hi!"
                1 -> "Hello!"
                2 -> "Yah!"
                else -> ""
            }

        }
    }

    @Test
    fun verifyでメソッドが何回呼ばれたか確認する() {
        val fuga  = spy(Fuga())

        fuga.saySomething(0)
        fuga.saySomething(0)
        fuga.saySomething(0)


        fuga.saySomething(1)
        fuga.saySomething(1)

        verify(fuga, times(3)).saySomething(0)
        verify(fuga, times(2)).saySomething(1)
    }

    @Test
    fun spyを理解する() {
        val fuga  = spy(Fuga())
        val mockValue = "wow!"

        `when`(fuga.saySomething(0)).thenReturn(mockValue)

        assert(fuga.saySomething(0) == mockValue) { fuga.saySomething(0) }
        assert(fuga.saySomething(1) == "Hello!") { fuga.saySomething(1) }
        assert(fuga.saySomething(2) == "Yah!") { fuga.saySomething(2) }
        assert(fuga.saySomething(3) == "") { fuga.saySomething(3) }
    }

    @Test
    fun WhenとThenを理解する2() {
        val fuga  = mock(Fuga::class.java)
        val mockValue = "wow!"

        `when`(fuga.saySomething(0)).thenReturn(mockValue)

        assert(fuga.saySomething(0) == mockValue) { fuga.saySomething(0) }
        assert(fuga.saySomething(1) == "Hello!") { fuga.saySomething(1) } // これは通らない！
    }

    @Test
    fun インジェクト用のOkHttp作成() {
        val okHttpClient = mock(OkHttpClient::class.java)
        val body = "hoge"
        val responseBody = mock(ResponseBody::class.java)
        `when`(responseBody.string()).thenReturn(body)

        val response = mock(Response::class.java)
        `when`(response.isSuccessful).thenReturn(true)
        `when`(response.body()).thenReturn(responseBody)

        val call = mock(Call::class.java)
        `when`(call.execute()).thenReturn(response)
        `when`(okHttpClient.newCall(any(Request::class.java))).thenReturn(call)

    }

    @Test
    fun HttpClientテスト() {
        val param = mapOf(
            "name" to "リクエスター(No.${ApiGenerator.mRequestCounter})",
            "message" to "こんにちは、世界",
            "response_type" to "string"
        )
        val httpClient = HttpClient()
        try {
            val response = httpClient.post("https://apppppp.com/post_echo.php", param)
            println(response)

        } catch (e:IOException) {
            println(e)
        }


    }

//    @Test
//    fun fetchEchoApiテスト() {
//
//
//        val callback = spy(DummyCallback)
//
//
//        ApiGenerator.fetchEchoApi(callback)
//
//
//        verify(callback, timeout(2000)).getResult()
//
////        assert(callback.getResult() != null, { "結果がnull!" })
//
//    }


}
//
//val DummyCallback = object: HttpRequestCallback() {
//    override fun onSuccess(responseBody: String) {
//        println(responseBody)
//    }
//
//}
