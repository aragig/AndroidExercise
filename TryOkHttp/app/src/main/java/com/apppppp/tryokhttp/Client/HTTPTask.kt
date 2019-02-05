package com.apppppp.tryokhttp.Client

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import java.lang.ref.WeakReference


class HTTPTask<T>(activity: T, private val client:HTTPClient) : AsyncTask<Void, Void, String?>() where T : AppCompatActivity, T : HTTPTask.Listener {


    interface Listener {
        fun didFinishedHTTPTask(result:String?)
    }


    var delegate = WeakReference(activity)


    override fun doInBackground(vararg params: Void): String? {
//        Thread.sleep( 2000)
        return client.request()
    }

    override fun onPostExecute(result: String?) {
//        super.onPostExecute(result)
        delegate.get()?.didFinishedHTTPTask(result)
    }

}