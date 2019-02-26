package com.apppppp.trydialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.apppppp.trydialog.R

class RadiobuttonDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {



        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            .setSingleChoiceItems(R.array.language_array, 1) { dialog, which: Int ->
                println(which)
            }
            .setPositiveButton("OK") { dialog, id ->
            }
            .setNegativeButton("Cancel") { dialog, id ->

            }


        return builder.create()
    }


}