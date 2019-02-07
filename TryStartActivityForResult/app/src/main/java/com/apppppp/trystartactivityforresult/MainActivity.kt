package com.apppppp.trystartactivityforresult

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val show = findViewById<Button>(R.id.show)
        show.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivityForResult(intent, 9)   // requestCodeであとから識別できる
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 9) { return }

        val result = findViewById<TextView>(R.id.result)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val message = data.getStringExtra("message")
            result.text = message

        } else if (resultCode == Activity.RESULT_CANCELED) {
            result.text = "端末の戻るボタンが押されました。"
        }

    }
}
