package com.apppppp.databinding.model

import android.databinding.ObservableField

class MainViewModel {
    val name = ObservableField("天照大神")

    fun onClickedButton() {
        name.set("月読尊")
    }
}