package com.apppppp.tryasynctaskloader

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.widget.TextView
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<String>> {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<String>> {
        val filePath = args?.getString("file_path") ?: throw IllegalArgumentException("file path not specified")
        return FileLoader(this, filePath)
    }

    override fun onLoadFinished(loader: Loader<List<String>>, data: List<String>?) {
        if (data != null) {
            val fileContent = data.joinToString("\n")
            val content = findViewById<TextView>(R.id.fileContent)
            content.text = fileContent
        }
    }

    override fun onLoaderReset(p0: Loader<List<String>>) {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSharedPreferences("sample", Context.MODE_PRIVATE).edit()
            .putInt("age", 100)
            .putString("name", "Hoge")
            .apply()

        val arguments = Bundle()
        arguments.putString("file_path", applicationInfo.dataDir + "/shared_prefs/sample.xml")
        supportLoaderManager.initLoader(1, arguments, this)

        /**
@Deprecated
public LoaderManager getLoaderManager() {
    throw new RuntimeException("Stub!");
}
         **/

    }

}
