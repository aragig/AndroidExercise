package com.apppppp.tryasynctaskloader

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class FileLoader (context: Context, private val filePath: String) : AsyncTaskLoader<List<String>>(context) {
    private var cache : List<String>? = null

    override fun loadInBackground(): List<String>? {
        val file = File(filePath)

        val reader = BufferedReader(FileReader(file))
        return reader.readLines()
    }

    override fun deliverResult(data: List<String>?) {
        if (isReset) { // isResetはLoaderクラスのメンバ
            return
        }
        cache = data
        super.deliverResult(data)
    }

    override fun onStartLoading() {
        if (cache != null) {
            deliverResult(cache)
        }
        if (takeContentChanged() || cache == null) { // takeContentChangedはLoaderクラスのメンバ
            forceLoad()
        }
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}