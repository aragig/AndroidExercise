package com.apppppp.trydialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.apppppp.trydialog.R

class ListDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            // .setMessage("Here Message") // setMessageは使うとリスト表示されないので注意！
            .setItems(R.array.language_array) { dialog, which ->
                val langs = resources.getStringArray(R.array.language_array)
                println(langs[which])

            }

        return builder.create()
    }

}