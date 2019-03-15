package com.apppppp.trycoroutinehttprequest

import android.util.Base64
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun エコーAPI_JSONでレスポンス() {
        try {
            val res = fetchEchoApiWithJson()
            println(res)
            assert(res != null)

        } catch (e: IOException) {
            assert(false) { e.toString() }

        }
    }


    @Test
    fun エコーAPI_Stringをレスポンス() {
        try {
            val res = fetchEchoApi()
            println(res)
            assert(res != null)

        } catch (e: IOException) {
            assert(false) { e.toString() }

        }
    }


    @Test
    fun ジョジョAPI() {
        try {
            val res = fetchJojoApi()
            println(res)
            assert(res != null)

        } catch (e: IOException) {
            assert(false) { e.toString() }

        }
    }


    @Test
    fun ハッシュマップ拡張関数() {
        val param = PostParams()
        param["key1"] = "value1"
        param["key2"] = "value2"

        val parameterString = param.toPostByteArray()
        println(parameterString)
        assert(true)
    }



}
