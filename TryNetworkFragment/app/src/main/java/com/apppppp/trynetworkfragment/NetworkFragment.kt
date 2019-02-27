package com.apppppp.trynetworkfragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.AsyncTask.execute
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val TAG = "NetworkFragment"
private const val URL_KEY = "UrlKey"

class NetworkFragment:Fragment() {
    private var mCallback: DownloadCallback<String>? = null
    private var downloadTask: DownloadTask? = null
    private var urlString: String? = null

    companion object {
        fun getInstance(fragmentManager: FragmentManager, url: String):NetworkFragment {
            val networkFragment = NetworkFragment()
            val args = Bundle()
            args.putString(URL_KEY, url)
            networkFragment.arguments = args
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit()
            return networkFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urlString = arguments?.getString(URL_KEY)
    }

    override fun onAttach(context: Context?) { // Activityが渡される
        super.onAttach(context)
        mCallback = context as? DownloadCallback<String>
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
        println("onDetach")
    }

    override fun onDestroy() {
        cancelDownload()
        println("onDestroy")
        super.onDestroy()
    }

    fun startDownload() {
        cancelDownload()
        mCallback?.also { callback ->
            downloadTask = DownloadTask(callback).apply {
                execute(urlString)
            }
        }
    }

    fun cancelDownload() {
        downloadTask?.cancel(true)
    }

}

private class DownloadTask(callback: DownloadCallback<String>): AsyncTask<String, Int, DownloadTask.Result>() {

    private var mCallback:DownloadCallback<String>? = null

    init {
        setCallback(callback)
    }

    internal fun setCallback(callback:DownloadCallback<String>) {
        mCallback = callback
    }

    internal class Result {
        var mResultValue: String? = null
        var mException: Exception? = null

        constructor(resultValue:String) {
            mResultValue = resultValue
        }

        constructor(exception: Exception) {
            mException = exception
        }
    }

    override fun onPreExecute() {
        if(mCallback != null) {
            val networkInfo = mCallback?.getActiveNetWorkInfo()
            if(networkInfo?.isConnected == false
                || networkInfo?.type != ConnectivityManager.TYPE_WIFI
                && networkInfo?.type != ConnectivityManager.TYPE_MOBILE) {
                mCallback?.updateFromDownload(null)
                cancel(true)
            }
        }
    }

    override fun doInBackground(vararg urls: String?): DownloadTask.Result? {
        var result: Result? = null
        if (!isCancelled && urls.isNotEmpty()) {
            val urlString = urls[0]
            result = try {
                val url = URL(urlString)
                val resultString = downloadUrl(url)
                if (resultString != null) {
                    Result(resultString)
                } else {
                    throw IOException("No response received.")
                }
            } catch (e: Exception) {
                Result(e)
            }
        }
        return result
    }

    override fun onPostExecute(result: Result?) {
        mCallback?.apply {
            result?.mException?.also { exception ->
                updateFromDownload(exception.message)
                return
            }
            result?.mResultValue?.also { resultValue ->
                updateFromDownload(resultValue)
                return
            }
            finishDownloading()
        }
    }

    override fun onCancelled(result:Result) {
        println("onCancelled")
    }

    @Throws(IOException::class)
    private fun downloadUrl(url: URL): String? {
        var connection: HttpsURLConnection? = null
        return try {
            connection = (url.openConnection() as? HttpsURLConnection)
            connection?.run {
                readTimeout = 3000
                connectTimeout = 3000
                requestMethod = "GET"
                doInput = true
                connect()
                publishProgress(CONNECT_SUCCESS)
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }
                publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                inputStream?.let { stream ->
                    readStream(stream, 500)
                }
            }
        } finally {
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }

    @Throws(IOException::class, UnsupportedEncodingException::class)
    fun readStream(stream: InputStream, maxReadSize:Int):String? {
        val reader: Reader? = InputStreamReader(stream, "UTF-8")
        val rawBuffer = CharArray(maxReadSize)
        val buffer = StringBuffer()
        var readSize:Int = reader?.read(rawBuffer) ?: -1
        var maxReadBytes = maxReadSize
        while (readSize != -1 && maxReadBytes > 0) {
            if (readSize > maxReadBytes) {
                readSize = maxReadBytes
            }
            buffer.append(rawBuffer, 0, readSize)
            maxReadBytes -= readSize
            readSize = reader?.read(rawBuffer) ?: -1
        }
        return buffer.toString()
    }
}