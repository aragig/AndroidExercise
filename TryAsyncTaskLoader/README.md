今回のプロジェクトはサードパーティのライブラリを使わずHttpClientを立ち上げ、FragmentとAsyncTaskを使ってネットワーク通信をするものである。[前回行ったFragmentを利用したダイアログ表示](https://www.101010.fun/entry/android-dialog-kotlin)でのコールバックメソッドの実装と同様、ネットワーク接続でも同じような考え方で実装することができる。
このことを[Androidデベロッパーガイド](https://developer.android.com/training/basics/network-ops/connecting)のサンプルを元に試してみたので、この記事にまとめておく。



## UIの流れ

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227102837.png)


1. MainActivityからボタンクリックでDownloadActivityへ遷移する。
2. Downloadボタンを押すとHTTPリクエストが行われる。
3. リクエスト先のHTTMLファイルをダウンロードする。
4. DownloadActivityをfinishさせ、リソースの開放をチェックする。

以上がUIの流れになる。



## クラス構成
クラス構成は次のとおりだ。

1. MainActivity (AppCompatActivity)
2. DownloadActivity (FragmentActivity)
3. DownloadCallback (interface)
4. NetworkFragment (Fragment)
5. DownloadTask (AsynkTask)



![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227115726.png)



あまりうまい絵ではないが、コールバックインタフェースを通して　アクティビティと非同期処理をやり取りするイメージである。



MainActivityは下記の通り、ボタン配置しただけの簡単なアクティビティなので説明は省略する。

```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toDownloadButton = findViewById<Button>(R.id.toDownload)
        toDownloadButton.setOnClickListener {
            val intent = Intent(this, DownloadActivity::class.java)
            startActivity(intent)
        }
    }

}
```

次からは2番以降のクラスを詳しく見ていくことにする。




## DownloadActivityクラス

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227120603.png)

まずダウンロードを行うためのDownloadActivityクラスを用意した。内容は次のとおりである。

```kotlin
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
```

DownloadActivityはFragmentActivityを継承したクラスだ。また次に述べるDownloadCallbackインタフェースを実装している。先頭から4つまでのメソッドが、そのインタフェースのメソッドをオーバーライドしている。

またonCreate内ではNetworkFragmentのインスタンスを生成している。downloadボタンを押すと、NetworkFragmentインスタンスの非同期処理がスタートするようになっている。

リクエスト先には、このブログのトップページを指定した。[https://www.101010.fun/](https://www.101010.fun/)



## DownloadCallbackインタフェース

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227120607.png)

次にダウンロードが完了した際に、ホスト先Activityへコールバックさせるためのインタフェースを定義する。ここではDownloadCallbackとした。内容は次のとおりだ。

```kotlin
const val ERROR = -1
const val CONNECT_SUCCESS = 0
const val GET_INPUT_STREAM_SUCCESS = 1
const val PROCESS_INPUT_STREAM_IN_PROGRESS = 2
const val PROCESS_INPUT_STREAM_SUCCESS = 3

interface DownloadCallback<T> {
    fun updateFromDownload(result: T?)

    fun getActiveNetWorkInfo(): NetworkInfo

    fun onProgressUpdate(ProgressCode:Int, percentComplete:Int)

    fun finishDownloading()
}
```

ホスト先ActivityはDownloadActivityとなるので、このインタフェースを実装することになる。




## NetworkFragment

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227120611.png)


```kotlin
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
```

NetworkFragmentはFragmentを継承したクラスだ。`companion object`が実装されているとおりNetworkFragmentはシングルトンオブジェクトとして扱うことになる。



またFragment内ではAsyncTaskが絡んでくるので少し複雑になっているが、Activityからはカプセル化されるために、逆にActivityのコードはスッキリさせることができる。ActivityとAsyncTaskの間にNetworkFragmentをはさんだおかげだ。



NetworkFragmentクラスのonAttachでホスト先アクティビティつまり、DownloadActivityをDownloadCallback型にキャストさせている。キャストされたオブジェクト(mCallback)はstartDownloadメソッド内でAsynkTaskに渡される。またonDetachではこのmCallbackオブジェクトの開放を行っている。mCallbackの参照はActivityなので、Activityを保持し続けてメモリリークとなってしまうことを避けるためだ。




## DownloadTask

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227120617.png)

最後にDownloadTaskクラスを実装する。このクラスはAsyncTaskを継承したクラスだ。スコープの問題でNetworkFragment.ktファイル内に記述していることに注意する。


```kotlin
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
```


プログラムを読むとDownloadTaskのコンストラクタでDownloadCallbackオブジェクト受け取っている。渡されたDownloadCallbackオブジェクトを使って、ホスト先のアクティビティへイベントを通知するのだ。

またHttpsURLConnectionを使ってHTTP通信を実装しているが、ここをOkHttpなどのライブラリを使って置き換えれば、もう少しプログラム内容を簡潔に書くことができるであろう。

DownloadTaskのその他の処理は、一般的なAsyncTaskの処理内容となるのでここでは詳しい説明は省く。




## サンプル実行

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190227/20190227102837.png)

最後にプロジェクトをビルドしてUIの流れのように実行してみよう。Downloadボタンを押せば、コンソールにリクエスト先のHTMLの内容が出力されるであろう。またMainActivityに戻ったときに、NetworkFragmentのonDestroyとonDettachが呼ばれているかどうかの確認もしよう。ちゃんと呼ばれていれば、ネットワーク処理関係のリソースが開放されるはずである。








最後にクドいかもしれないが、ネットワークを利用するのでAndroidのマニュフェストには下記の記述を忘れずに。

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```



## 参考
* [Androidデベロッパーガイド](https://developer.android.com/training/basics/network-ops/connecting)
