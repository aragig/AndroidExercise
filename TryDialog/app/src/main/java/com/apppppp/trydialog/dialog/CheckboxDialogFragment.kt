package com.apppppp.trydialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.apppppp.trydialog.R

class CheckboxDialogFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val checkedItems = booleanArrayOf(false, true ,false) // 保存されたデータに置き換えることができる
        val mSelectedItems:MutableList<Int> = mutableListOf()
        setupSelectedItems(checkedItems, mSelectedItems)


        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            // .setMessage("Here Message") // setMessageは使うとリスト表示されないので注意！
            .setMultiChoiceItems(R.array.language_array, checkedItems) { dialog, which, isChecked ->
                if (isChecked) {
                    mSelectedItems.add(which)
                } else {
                    mSelectedItems.remove(which)
                }
            }
            .setPositiveButton("OK") { dialog, id ->
                printSelectedStatus(mSelectedItems)
            }
            .setNegativeButton("Cancel") { dialog, id ->

            }


        return builder.create()
    }

    private fun setupSelectedItems(
        checkedItems: BooleanArray,
        mSelectedItems: MutableList<Int>
    ) {
        var index = 0
        checkedItems.forEach {
            if (it) {
                mSelectedItems.add(index)
            }
            index++
        }
    }

    private fun printSelectedStatus(mSelectedItems: MutableList<Int>) {
        val langs = resources.getStringArray(R.array.language_array)
        mSelectedItems.forEach {
            println(langs[it])
        }
    }

}