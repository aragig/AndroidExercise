![](http://f.hatena.ne.jp/araemonz/20190208002302)

前回に引き続き、この本で学んだことをメモしておく。今回は`startActivityForResult`について。

<a target="_blank"  href="https://www.amazon.co.jp/gp/product/479739580X/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=479739580X&linkCode=as2&tag=101010fun-22&linkId=55499b04048d89edc1454fb6792fb739"><img border="0" src="//ws-fe.amazon-adsystem.com/widgets/q?_encoding=UTF8&MarketPlace=JP&ASIN=479739580X&ServiceVersion=20070822&ID=AsinImage&WS=1&Format=_SL250_&tag=101010fun-22" ></a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=479739580X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />


Activity間の画面遷移におけるデータの受け渡しは、シングルトンやPreferenceなどでパターンでやっていたが、簡単なものなら`startActivityForResult`を使えば便利そうだ。
ここでは単純なモデルで、`startActivityForResult`を使ったデータの受け渡しの動作確認をしていこう。



##  仕様
MainActivityとSubActivityを作って、このように遷移させる。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208002307.png)

SubActivityからメッセージを受け取って、MainActivityのTextViewへ反映させるといったシンプルなものだ。また、端末の`戻るボタン`で戻った場合には、違うメッセージを表示させる。




## MainActivity
それでは、MainActivityから見ていこう。

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val show = findViewById<Button>(R.id.show)
        show.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivityForResult(intent, 9)   // AcrequestCodeで複数Activityに対応できる
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 9) { return }

        val result = findViewById<TextView>(R.id.result)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val message = data.getStringExtra("message")
            result.text = message

        } else if(resultCode == Activity.RESULT_CANCELED) {
            result.text = "端末の戻るボタンが押されました。"
        }

    }
}
```
`startActivityForResult(intent, 9)` では、AcrequestCodeに値をセットすることで、Activityごとに処理を分けることができる。

`onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)` は、戻ったときにコールバックされるメソッドだ。ここでは、resultCodeでActivityを識別して、Intentからデータを取り出している。また、Activity.RESULT_CANCELEDの場合は、端末の戻るボタンが押されたものと判断させている。



## SubActivity

次に、遷移先であるSubActivityを見ていこう。
```kotlin
class SubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        // 最初にキャンセルされた結果をセットしておくことで、端末の戻るボタンに対応させる
        setResult(Activity.RESULT_CANCELED) // 無くても動く？

        val close = findViewById<Button>(R.id.close)
        close.setOnClickListener {

            val result = Intent()
            result.putExtra("message", "Hello! I'm from SubActivity!⛄️")
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}

```

`setResult(Activity.RESULT_CANCELED)`で予めキャンセル指定している。最初にキャンセルされた結果をセットしておくことで、端末の戻るボタンでRESULT_CANCELEDが認識されるようにしている。しかし、デフォルトでRESULT_CANCELEDが設定されているようなので、この記述はなくても良いのかもしれない。
`setResult(Activity.RESULT_OK, Intent)`では、渡したいデータをIntentを使ってセットしている。この場合、キーを"message"とし、値に "Hello! I'm from SubActivity!⛄️" をセットしている。

以上のようにプログラムすることで、最初の図のような画面結果が得られる。



## Intentの役割
![](http://f.hatena.ne.jp/araemonz/20190208002302)

[「基本からしっかり身につくAndroidアプリ開発入門」](https://www.amazon.co.jp/gp/product/479739580X/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=479739580X&linkCode=as2&tag=101010fun-22&linkId=7780fb8251672fd12a4b940b0266376b) の117ページに書かれているとおり、コンポーネントは、他のコンポーネントを呼び出すことができる。そのときに、情報をメッセージとしてAndroidシステムに渡す仕組みをIntentと呼ぶ。ここでのコンポーネントとは、アクティビティやサービスのことである。
つまり、ActivityからActivityへ直接値を渡しているのではないということ。AndroidシステムへIntentを投げて、それに適合したActivityが呼び出されるといったイメージだ。





## 参考
<a target="_blank"  href="https://www.amazon.co.jp/gp/product/479739580X/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=479739580X&linkCode=as2&tag=101010fun-22&linkId=55499b04048d89edc1454fb6792fb739"><img border="0" src="//ws-fe.amazon-adsystem.com/widgets/q?_encoding=UTF8&MarketPlace=JP&ASIN=479739580X&ServiceVersion=20070822&ID=AsinImage&WS=1&Format=_SL250_&tag=101010fun-22" ></a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=479739580X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />

