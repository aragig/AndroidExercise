package com.apppppp.tryflavor.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.apppppp.tryflavor.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("This is demo flavor.")
    }
}
