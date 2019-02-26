package com.apppppp.trydialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.lang.ClassCastException

class NoticeDialogFragment: DialogFragment() {
    public interface NoticeDialogLister {
        public fun onDialogPositiveClick(dialog:DialogFragment)
        public fun onDialogNegativeClick(dialog:DialogFragment)
    }

    var mLister:NoticeDialogLister? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mLister = context as NoticeDialogLister
        } catch (e: ClassCastException) {
            throw ClassCastException("${context.toString()} must implement NoticeDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            .setMessage("Here Message")
            .setPositiveButton("ok") { dialog, id ->
                println("dialog:$dialog which:$id")
                mLister?.onDialogPositiveClick(this)
            }
            .setNegativeButton("cancel") { dialog, id ->
                println("dialog:$dialog which:$id")
                mLister?.onDialogNegativeClick(this)
            }

        return builder.create()
    }


    override fun onDestroy() {
        println("NoticeDialogFragmentのonDestroyが呼ばれたよ！")
        super.onDestroy()
    }

    override fun onDetach() {
        println("NoticeDialogFragmentのonDetachが呼ばれたよ！")
        super.onDetach()
    }


}