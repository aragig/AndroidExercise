package com.apppppp.tryvolley

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** RequestQueueはApplication Contextで初期化すべき **/
//        MySingleton.getInstance(this.applicationContext).requestQueue
    }

    fun requestString(view:View) {
        val textView = findViewById<TextView>(R.id.string_message)

        val url = "https://www.google.com"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { textView.text = "That didn't work!" })
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

    fun requestJsonObject(view: View) {
        val textView = findViewById<TextView>(R.id.json_message)

        val url = "https://apppppp.com/jojo.json"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                textView.text = "名前:${response.getString("name")} / スタンド:${response.getString("stand")}"
            },
            Response.ErrorListener { textView.text = "That didn't work!" })
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }
}

