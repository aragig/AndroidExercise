package com.apppppp.trydialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.EditText
import com.apppppp.trydialog.R

class SigninDialogFragment:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val signinView = inflater.inflate(R.layout.dialog_signin, null)

        builder.setView(signinView)
            .setTitle("Sign in")
            .setPositiveButton("OK") { dialog, id ->
                val email = signinView.findViewById<EditText>(R.id.email).text
                val password = signinView.findViewById<EditText>(R.id.password).text
                println("Email: $email Password:$password")
            }
            .setNegativeButton("Cancel") { dialog, id ->

            }

        return builder.create()
    }
}