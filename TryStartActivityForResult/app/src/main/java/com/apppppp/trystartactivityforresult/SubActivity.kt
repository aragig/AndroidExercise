package com.apppppp.trystartactivityforresult

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        // 最初にキャンセルされた結果をセットしておくことで、端末の戻るボタンに対応させる
        setResult(Activity.RESULT_CANCELED) // なくても動く？

        val close = findViewById<Button>(R.id.close)
        close.setOnClickListener {

            val result = Intent()
            result.putExtra("message", "Hello! I'm from SubActivity!⛄️")
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }


}
