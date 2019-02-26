package com.apppppp.trycoroutine

import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.Serializable
import java.lang.Exception
import java.net.URL


const val CHARACTER_DATA_API = "https://apppppp.com/jojo.json"

object CharacterGenerator {
    data class CharacterData(val name: String,
                             val stand: String) : Serializable

    fun fromApiData(apiData: String): CharacterData
        = Gson().fromJson(apiData, CharacterData::class.java)

}

fun fetchCharacterData(): Deferred<CharacterGenerator.CharacterData?> {
    return GlobalScope.async {
        // wifiオフにすると例外がスローされる
        // java.net.ConnectException: Failed to connect to ....
        val apiData:String? =
            try {
                URL(CHARACTER_DATA_API).readText()
            } catch(e:Exception) {
                null
            }

        apiData?.let{ CharacterGenerator.fromApiData(apiData) }
        // apiDataがnullなら、ここではなにもせずにasyncがリターンされる?
    }
}

