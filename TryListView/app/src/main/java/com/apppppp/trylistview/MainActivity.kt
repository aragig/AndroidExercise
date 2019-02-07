package com.apppppp.trylistview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val list = findViewById<ListView>(R.id.clockList)
        val adapter = TimeZoneAdapter(this)

        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->

            val timeZone = adapter.getItem(position)
            println(timeZone)
        }

    }
}
