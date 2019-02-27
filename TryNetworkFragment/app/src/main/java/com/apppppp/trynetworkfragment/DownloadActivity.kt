package com.apppppp.trynetworkfragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button

class DownloadActivity : FragmentActivity(), DownloadCallback<String> {

    override fun updateFromDownload(result: String?) {
        println(result)
    }

    override fun getActiveNetWorkInfo(): NetworkInfo {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

    override fun onProgressUpdate(ProgressCode: Int, percentComplete: Int) {
        when (ProgressCode) {
            ERROR -> {
            }
            CONNECT_SUCCESS -> {
            }
            GET_INPUT_STREAM_SUCCESS -> {
            }
            PROCESS_INPUT_STREAM_IN_PROGRESS -> {
            }
            PROCESS_INPUT_STREAM_SUCCESS -> {
            }
        }
    }

    override fun finishDownloading() {
        downloading = false
        networkFragment?.cancelDownload()
    }

    private var networkFragment: NetworkFragment? = null
    private var downloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        networkFragment = NetworkFragment.getInstance(supportFragmentManager, "https://www.101010.fun/")

        val download = findViewById<Button>(R.id.download)
        download.setOnClickListener {
            startDownload()
        }

        val close = findViewById<Button>(R.id.close)
        close.setOnClickListener {
            finish()
        }
    }

    private fun startDownload() {
        if(!downloading) {
            networkFragment?.apply {
                startDownload()
                downloading = true
            }
        }
    }
}
