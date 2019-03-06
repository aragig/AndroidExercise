package com.apppppp.trytouchmove

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView


private const val TOUCHABLE_FRAGMENT_TAG = "touchableFragment"

class MainActivity : AppCompatActivity(), TouchableFragment.OnTouchableFragmentListener {


    override fun actionDown() {
        @SuppressLint("SetTextI18n")
        resultTextView.text = "Action Down\n${resultTextView.text}"
    }

    override fun actionMove(dy: Float) {
        @SuppressLint("SetTextI18n")
        resultTextView.text = "dy: $dy\n" +
                "${resultTextView.text}"
    }

    override fun actionUp() {
        @SuppressLint("SetTextI18n")
        resultTextView.text = "\nAction Up\n" +
                "${resultTextView.text}"
    }

    lateinit var resultTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result)

        if (supportFragmentManager.findFragmentByTag(TOUCHABLE_FRAGMENT_TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, TouchableFragment.newInstance("hoge", "fuga"), TOUCHABLE_FRAGMENT_TAG)
                .commit()
        }
    }


}
