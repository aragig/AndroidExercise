![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190205/20190205021108.jpg)


今回は、[OkHttp](https://square.github.io/okhttp/)を使ってHTTPリクエストの動作確認を行ってみた。また既存のHttpURLConnectionバージョンとも比較してみた。





## やること

OkHttpと、Java / Kotlin標準のHttpURLConnectionを使って、GET、POSTリクエストの動作確認を行った。



## 完成イメージ(MainActivityからの呼び出し)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190205/20190205203425.png)

MainActivityからの事項例はこの様になる。
```kotlin
class MainActivity : AppCompatActivity(), HTTPTask.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1 = findViewById<Button>(R.id.client1)
        button1.setOnClickListener {
            doOkHttpClient()
        }
        val button2 = findViewById<Button>(R.id.client2)
        button2.setOnClickListener {
            doPostOkHttpClient()
        }
        val button3 = findViewById<Button>(R.id.client3)
        button3.setOnClickListener {
            doVerboseClient()
        }
        val button4 = findViewById<Button>(R.id.client4)
        button4.setOnClickListener {
            doPostVerboseClient()
        }
    }


    val requestURL = "https://apppppp.com/echo.php"

    fun doOkHttpClient() {
        val client = OkHttpClient(requestURL)
        doTask(client)
    }
    fun doPostOkHttpClient() {
        val client = OkHttpClient(requestURL, "body=やっほー!&name=OkHttpClient")
        doTask(client)
    }
    fun doVerboseClient() {
        val client = VerboseHttpClient(requestURL)
        doTask(client)
    }
    fun doPostVerboseClient() {
        val client = VerboseHttpClient(requestURL, "body=やっほー!&name=VerboseHttpClient")
        doTask(client)
    }

    fun doTask(client: HTTPClient) {
        val task = HTTPTask(this, client)
        task.execute()
    }

    override fun didFinishedHTTPTask(result:String?) {
        val resultText = findViewById<TextView>(R.id.result)
        resultText.text = result ?: "結果がNullだよ！"
    }
}
```
各メソッドでやっていることは単純だ。

1. HTTPクライアント作成
2. バックグラウンドタスクへ委譲
3. コールバックを受け取り、値をViewへ反映



サーバーにはGETとPOSTで出力メッセージを切り替えるだけの、簡単なプログラムを置いた。

```php
<?php
    $name = $_POST["name"];
    $body = $_POST["body"];
    if($name && $body) {
            echo $name."\n".$body;
    } else {
            echo "Hello Getter!";
    }
?>
```






## HTTPクライアントのインタフェースを作る

HTTPクライアントをバックグラウンドタスクへ委譲するため、このようなインタフェースを作ることにした。

```kotlin
interface HTTPClient {
    val url:String
    val body:String?
    fun request(): String?
}
```



このインタフェースを実装して、`OkHttpClient`および`VerboseHttpClient`を作ることにする。

```kotlin
class OkHttpClient(_url:String, _body:String? = null):HTTPClient {

    override val url: String = _url
    override val body:String? = _body

    /**********************************
     * http://square.github.io/okhttp/
     **********************************/

    var client = OkHttpClient()

    override fun request(): String? {
        val builder = Request.Builder()
        val request = if(body != null) {
            Log.d("mopi", body)

            val requestBody = buildMultipartBody(body)

            builder.url(url).post(requestBody).build()
        } else {
            builder.url(url).build()
        }

        client.newCall(request).execute().use { response -> return response.body()?.string() }
    }


    fun buildMultipartBody(body:String):MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for ((k, v) in body.toParams()) {
            builder.addFormDataPart(k, v)
        }
        return builder.build()
    }

    fun String.toParams():List<List<String>> = this
        .split(Regex("&"))
        .map { it.split(Regex("=")) }
}
```





```kotlin
class VerboseHttpClient(_url:String, _body:String? = null):HTTPClient {

    override val url: String = _url
    override val body:String? = _body

    private fun isCurrent(): Boolean {
        return Thread.currentThread() == getMainLooper().thread
    }

    override fun request(): String? {
        var result: String? = null
        var conn: HttpURLConnection? = null
        try {
            Log.d("mopi", "isCurrent ${isCurrent()}")

            conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = if(body == null) "GET" else "POST"
            conn.connectTimeout = 2000
//            conn.readTimeout = 10000
//            conn.doOutput = false
            conn.connect()

            if(body != null) {
                val oStream = conn.outputStream
                oStream.write(body.toByteArray())
                oStream.flush()
                oStream.close()
            }

            if (conn.responseCode == 200) {
                val iStream = conn.inputStream
                result = iStream.bufferedReader().use { it.readText() }
            }
        } finally {
            conn?.disconnect()
        }
        return result
    }

}
```





## OkHttpClientでPOSTのBodyを用意する

OkHttpではPOSTのパラメータを渡すときに、次のようにビルダーで生成しなければならない。

```kotlin
fun buildMultipartBody(body:String):MultipartBody {
    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

    for ((k, v) in body.toParams()) {
        builder.addFormDataPart(k, v)
    }
    return builder.build()
}
```



`key1=value1&key2=value2` のような形式の文字列は、そのまま渡せないのでパースする。

Stringクラスを拡張してパースしてみた。

```kotlin
fun String.toParams():List<List<String>> = this
	.split(Regex("&"))
	.map { it.split(Regex("=")) }

```





## AsyncTaskを継承したHTTPTask

HTTPTask`は次の様になっている。

```kotlin
class HTTPTask<T>(activity: T, private val client:HTTPClient) : AsyncTask<Void, Void, String?>() where T : AppCompatActivity, T : HTTPTask.Listener {

    interface Listener {
        fun didFinishedHTTPTask(result:String?)
    }

    var delegate = WeakReference(activity)

    override fun doInBackground(vararg params: Void): String? {
        return client.request()
    }

    override fun onPostExecute(result: String?) {
        delegate.get()?.didFinishedHTTPTask(result)
    }
}
```

コンストラクトに渡されるジェネリック型Tのactivityは、where以降で制約している通り、AppCompatActivityとHTTPTask.Listenerを実装している必要がある。

循環参照対策として、渡されたActivityはWeakReferenceに指定している。

こちらの記事を大いに参考させてもらった。

[https://qiita.com/niba1122/items/89813b9746f3d1d7979b:title]







