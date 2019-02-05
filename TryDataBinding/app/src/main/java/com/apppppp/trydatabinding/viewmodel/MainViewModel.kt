package com.apppppp.trydatabinding.viewmodel

import android.databinding.ObservableField
import android.util.Log
import android.view.View

class MainViewModel {

    var message = ObservableField("こんにちは、世界！")

    fun onClickButton(v:View) {
        Log.d("mopi", "$v")
        message.set("Successfully, DataBinding!")
    }
}
