package com.apppppp.trydialog

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Button
import com.apppppp.trydialog.dialog.*

class MainActivity : AppCompatActivity(), NoticeDialogFragment.NoticeDialogLister {

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        println("NoticeDialogでOKボタンが押されたよ！")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        println("NoticeDialogでCancelボタンが押されたよ！")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val simpleButton = findViewById<Button>(R.id.simple)
        simpleButton.setOnClickListener {
            val dialog = SimpleDialogFragment()
            dialog.show(supportFragmentManager, "simple")
        }

        val listButton = findViewById<Button>(R.id.list)
        listButton.setOnClickListener {
            val dialog = ListDialogFragment()
            dialog.show(supportFragmentManager, "list")
        }

        val checkboxButton = findViewById<Button>(R.id.checkbox)
        checkboxButton.setOnClickListener {
            val dialog = CheckboxDialogFragment()
            dialog.show(supportFragmentManager, "checkbox")
        }

        val radioButton = findViewById<Button>(R.id.radio)
        radioButton.setOnClickListener {
            val dialog = RadiobuttonDialogFragment()
            dialog.show(supportFragmentManager, "radio")
        }

        val signinButton = findViewById<Button>(R.id.signin)
        signinButton.setOnClickListener {
            val dialog = SigninDialogFragment()
            dialog.show(supportFragmentManager, "signin")
        }

        val noticeButton = findViewById<Button>(R.id.notice)
        noticeButton.setOnClickListener {
            val dialog = NoticeDialogFragment()
            dialog.show(supportFragmentManager, "notice")
        }

    }
}
