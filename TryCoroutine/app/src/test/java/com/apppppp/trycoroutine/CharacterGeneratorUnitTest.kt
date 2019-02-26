package com.apppppp.trycoroutine

import org.junit.Test

import org.junit.Assert.*

import com.apppppp.trycoroutine.CharacterGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CharacterGeneratorUnitTest {

    @Test
    fun データクラスの値チェック() {
        val characterData = CharacterGenerator.CharacterData("ジョルノ・ジョバーナ", "ゴールドエクスペリエンス")
        assertEquals("ジョルノ・ジョバーナ", characterData.name)
        assertEquals("ゴールドエクスペリエンス", characterData.stand)
    }

    @Test
    fun GSONでちゃんとパースできるか() {
        val characterData = CharacterGenerator.fromApiData("{name:\"ジョルノ・ジョバーナ\", stand:\"ゴールドエクスペリエンス\"}")
        assertEquals("ジョルノ・ジョバーナ", characterData.name)
        assertEquals("ゴールドエクスペリエンス", characterData.stand)
    }

    @Test
    fun JSONをフェッチする() {
        val result = URL(CHARACTER_DATA_API).readText()
        assertNotEquals(result, "")
    }

    @Test
    fun JSONをフェッチしてGSONパースする() {
        val data = URL(CHARACTER_DATA_API).readText()
        val characterData = CharacterGenerator.fromApiData(data)
        assertEquals("Jyotaro", characterData.name)
        assertEquals("The World", characterData.stand)
    }

    @Test
    fun コルーチン無しでfetchCharacterDataをテスト() {
        GlobalScope.launch(Dispatchers.Default) {
            val characterData = fetchCharacterData().await()
            assertEquals("Jyotaro", characterData?.name)
            assertEquals("The World!", characterData?.stand)
        }

    }

}
