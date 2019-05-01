[:contents]


## Volleyとは

VolleyとはAndroidアプリ開発におけるネットワーク通信を、簡単に記述することが出来るライブラリだ。
Volleyはキャッシュ考慮したHTTPリクエストを行ってくれる。
またレスポンスはメインスレッド（UIスレッド）で返されるので、`AsyncTask`などを使った自前の非同期処理を書かなくて済むのだ。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190303/20190303003126.jpg)

図のようにキャッシュが存在していればキャッシュからデータを即座に返す。キャッシュがない場合はHTTPリクエストを行い結果をすようになっている。同時にキャッシュにリクエスト結果も保存されるのである。このようなことをVollyeでは自動で行ってくれるのだ。

処理の流れもプログラマーに優しく理解しやすいだろう。
今回はこのVolleyを使って簡単な動作確認を行ってみたいと思う。



## パーミッションの許可

ネットワークを使うので忘れずに下記の記述をマニフェストに追記しておこう。

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Volleyライブラリをインストールする

VolleyをAndroidで使用できるようにするには、モジュールレベルの`build.gradle`の`dependencies`へ下記のように追記する。

```
dependencies {
	...
    implementation 'com.android.volley:volley:1.1.1'
}
```


## RequetQueueをシングルトンで管理する

![](http://f.hatena.ne.jp/araemonz/20190303004753)

Volleyでは`RequetQueue`というシステムでリクエストを管理している。[ドキュメント(Set up RequestQueue)](https://developer.android.com/training/volley/requestqueue)によれば、`RequetQueue`はシングルトンを使って生成することが推奨されている。シングルトンの作り方はドキュメントに載っているサンプルプログラムを使わせてもらうことにしよう。


```kotlin
class MySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE:MySingleton? = null
        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: MySingleton(context).also {
                        INSTANCE = it
                    }
                }
    }
    val requestQueue:RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}
```

例えば`Activity`からリクエストを`RequestQueue`に追加するには次の様になる。

```kotlin
MySingleton.getInstance(this).addToRequestQueue(stringRequest)
```



## サンプルプロジェクト

実際にサンプルプロジェクトを新規作成し、Volleyを実装してみよう。

[f:id:araemonz:20190303123931j:plain]

図のような`TextView`と`Button`を2つづつ配置して動作確認を行っていく。

ボタンをクリックしたらVolleyを使ってHTTPリクエストを投げ、返ってきた結果を表示するという簡単なプログラムである。

1つ目のボタンのアクションは次のとおりである。

```kotlin
fun requestString(view:View) {
	val textView = findViewById<TextView>(R.id.string_message)

	val url = "https://www.google.com"

	val stringRequest = StringRequest(
		Request.Method.GET, url,
		Response.Listener<String> { response ->
			textView.text = "Response is: ${response.substring(0, 500)}"
		},
		Response.ErrorListener { textView.text = "That didn't work!" })
	MySingleton.getInstance(this).addToRequestQueue(stringRequest)
}
```

`StringRequest`を使ったリクエストを行っている。このメソッドでは`Response.Listener`で返される`respionse`がString型になっていることが特徴だ。また`Response.Listener`で渡しているラムダ式はメインスレッド上で実行されるので、上記のように`TextView`などの値の変更を行える。

`MySingleton.getInstance(this).addToRequestQueue(stringRequest)`は先程説明したとおり、シングルトンを使った`RequestQueue`へ`StringRequest`を追加している。


次に2つ目のボタンのアクションをみてみよう。

```kotlin
fun requestJsonObject(view: View) {
	val textView = findViewById<TextView>(R.id.json_message)

	val url = "https://apppppp.com/jojo.json"

	val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
		Response.Listener { response ->
			textView.text = "名前:${response.getString("name")} / スタンド:${response.getString("stand")}"
		},
		Response.ErrorListener { textView.text = "That didn't work!" })
	MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
}
```

今度は`JsonObjectRequest`を使ったリクエストを行っている。`StringRequest`の処理と大変似ていることがわかるだろう。`JsonObjectRequest`の第三引数にはJson形式で値をpostできるようだが、今回は使用しないので`null`としている。

またリクエスト後に返ってくるJSONデータは次のようなものである。

```json
{
"name":"Jotaro",
"stand":"The World"
}
```

`Response.Listener`で返される`response`は`JSONObject`型であることに注意しよう。`JSONObject`型では`response.getString("name")`のような形で値へアクセスすることができる。




## まとめ

Volleyの機能としては他にもキャンセル処理や画像の読み込みなどに特化したものもある。これらの機能は別の機会に試したいと思う。


今回のサンプルプログラムはGitHubへ公開しておく。

[Volleyサンプルプログラム](https://github.com/araemon/AndroidExercise/tree/master/TryVolley)



## 参考

* https://developer.android.com/training/volley
* http://techbooster.org/android/hacks/16474/


