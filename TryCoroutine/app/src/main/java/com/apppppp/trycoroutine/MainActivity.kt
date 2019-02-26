package com.apppppp.trycoroutine

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    // 通信障害などによるデータ取得できなかった場合を考えて、Nullableにした
    private var characterData:CharacterGenerator.CharacterData? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var count = 0


        val generateButton = findViewById<Button>(R.id.generateButton)
        generateButton.setOnClickListener {
//            characterData = CharacterGenerator.fromApiData("Jyotaro,The World") // 動作確認テスト
//            displayCharacterData()
            // 通常だとクラッシュすることを確認
            // android.os.NetworkOnMainThreadException


            // GlobalScope.launch(Dispatchers.Unconfined)だと次のエラーが、、、
            // android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
            GlobalScope.launch(Dispatchers.Main) {
                characterData = fetchCharacterData().await()
                displayCharacterData() // DataBindingの利用すれば、ここも省略できる
            }
        }
    }

    fun displayCharacterData() {

        val characterTextView = findViewById<TextView>(R.id.characterTextView)
        @SuppressLint("SetTextI18n")
        // JSONデータが取得できなかった場合、Errorと表示することにした
        characterTextView.text = """
            name  : ${characterData?.name ?: "Error"}
            stand : ${characterData?.stand ?: "Error"}
        """.trimIndent()
    }

}

