package com.apppppp.trycoroutinehttprequest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
    }

    fun setup() {
        show.setOnClickListener {
            Intent(this, SubActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

}