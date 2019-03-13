![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190203/20190203211352.jpg)

Retrofitを使った説明はたくさんあるが、デザインパターンを埋め込んで説明しているものが多く、肝心のRetrofitの役割がどこからどこまでなのかイメージし辛く分かりづらかった。
そこで、MainActivity.ktファイルだけに必要なRetrofit関係のコードを書いていてみた。
最小限のサンプルではあるが、動作を理解する上ではとてもわかりやすくなった。




## 完成イメージ

**Retrofitとは**、APIから取得するJSONデータを、オブジェクトにしてアクセスできるようにするまでを面倒見てくれる**HTTP clientだ**。
ここではRetrofitを使って、APIから拾ったデータを下の図のようにViewへ表示させるまでをやってみたいと思う。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190203/20190203210322.png)

いきなりだが全コードを記そう。

```kotlin
package com.apppppp.retrofitsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.concurrent.thread



data class JoJo(val name: String = "", val stand: String = "")


interface JoJoService {
//    -------レスポンスの中身-------
//    {
//        "name":"Jyotaro",
//        "stand":"The World"
//    }
//    -------おわり-----------

    @GET("jojo.json")
    fun fetchJoJo(): Call<JoJo>
}



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://apppppp.com/")
            .addConverterFactory(GsonConverterFactory.create())
//           ↑↑↑↑↑↑↑↑
//             Gsonのファクトリーメソッド必須！

//            これがないと次のようなエラーが出てハマる
//            java.lang.IllegalArgumentException: Unable to create converter for class com.apppppp.retrofitsample.JoJo
//            for method JoJoService.fetchJoJo
//
            .build()


        val handler = Handler()

        thread { // Retrofitはメインスレッドで処理できない
            try {
                val service:JoJoService = retrofit.create(JoJoService::class.java)
                val jojo = service.fetchJoJo().execute().body() ?: throw IllegalStateException("bodyがnullだよ！")

                handler.post(Runnable {
                    // メインスレッドで処理
                    val nameTextView = findViewById<TextView>(R.id.name)
                    val standTextView = findViewById<TextView>(R.id.stand)

                    nameTextView.text = jojo.name
                    standTextView.text = jojo.stand
                })


            } catch (e: Exception) {
                Log.d("mopi", "debug $e")
            }
        }

    }
}
```

以外に短くて、拍子抜けしたかもしれない。

なお、本サンプルはGithubへあげておいた。記事ページ下のリンクを参照されたし。



それでは手順を追って説明していく。



## APIの準備

まず、自前のAPIを用意する。GithubやQiitaのような本格的なものではなく、もっと**シンプルで単純なもののほうが理解を早める**からだ。それに、自分で作ったものなのでAPIの仕様を変更できるし興味もわく。

そういうことで、次のような内容のJSONファイルをサーバー上へ置いた。
```
{
    "name":"Jyotaro",
    "stand":"The World"
}
```
URLはこのようになっている。http://apppppp.com/jojo.json




## Retrofitの実装 (implementation)


Retrofitを使えるようにするため、新規プロジェクトの`build.gradle (Module: app)`に次のように追加する。
```
dependencies {
	...	
    implementation 'com.squareup.retrofit2:retrofit:2.5.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
    implementation 'com.google.code.gson:gson:2.8.5'
}
```
これは、[retrofit2最新版](https://square.github.io/retrofit/)と、GSON、そしてRetrofit用のGSONコンバーターを指定しているのだ。




## データクラスの作成

JSONファイルの内容と合わせて、次のようなデータクラスを定義した。ただし、JSONのキーの名前とプロパティの名前は同じにすること。
```kotlin
data class JoJo(val name: String = "", val stand: String = "")
```



## インタフェースの作成

次にインタフェースを作成する。
```kotlin
interface JoJoService {
    @GET("jojo.json")
    fun fetchJoJo(): Call<JoJo>
}
```
`ドメイン以下のパス`をGETアノテーションの引数に渡す。つまりこの場合だと、ドメイン直下にファイルを置いたので`jojo.json`を渡せば良い。

fetchJoJoメソッドは、APIリクエストを行う。定義をするだけで、実装はやる必要はない。

なんと素晴らしいことに、**Retrofixが実装をやってくれるのだ！**
ところで、fetchJoJoメソッドで返されるCallオブジェクトは、 `import retrofit2.Call` でインポートしているものだ。一度どんなものか、仕様を見ておくと良いだろう。




## MainActivity

最後にMainActivityクラスを見ていこう。


ここでは、Retrofitオブジェクトを次のようにして生成する。
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://apppppp.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```
注意してほしいのは `addConverterFactory(GsonConverterFactory.create())` だ。
今回GSONを使用するので、この**ファクトリメソッドを設定しないとJSONをオブジェクトに変換できない**。以下のようなエラーが出たら、ここを疑ってほしい。

```
java.lang.IllegalArgumentException: Unable to create converter for class com.apppppp.retrofitsample.JoJo
            for method JoJoService.fetchJoJo
```



それでは、先程のRetrofitオブジェクトを実際に使っていこう。メインスレッドでは実行できないので、thread内で実行していく。

`retrofit.create(JoJoService::class.java)`では、先ほど作ったインタフェースのクラスオブジェクトを渡している。

```kotlin
val handler = Handler()

thread { // Retrofitはメインスレッドで処理できない
    try {
        val service:JoJoService = retrofit.create(JoJoService::class.java)
        val jojo = service.fetchJoJo().execute().body() ?: throw IllegalStateException("bodyがnullだよ！")

		....

    } catch (e: Exception) {
        Log.d("mopi", "debug $e")
    }
}
```

スレッド内から、UIを更新したい場合はこのように記述する。
```kotlin
handler.post(Runnable {
    // メインスレッドで処理
    val nameTextView = findViewById<TextView>(R.id.name)
    val standTextView = findViewById<TextView>(R.id.stand)

    nameTextView.text = jojo.name
    standTextView.text = jojo.stand
})

```
以上でRetrofitを使ったサンプルコードの出来上がりだ。



おっと、忘れていた！次の記述がマニフェストに無いと、インターネットアクセス出来ないのでご注意を。
```
<uses-permission android:name="android.permission.INTERNET" />
```



