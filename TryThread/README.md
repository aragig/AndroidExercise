Kotlinで`Thread`クラスでのスレッドの作成とキャンセル処理を試してみた。



## サンプルプロジェクトの全体の内容


![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190310/20190310164725.gif)


画像のようにStartボタンを押すとスレッドを新規作成して処理を走らせる。スレッドで行う処理は1秒間隔でカウンタを1づつ上げそれを標準出力するものである。そしてStopボタンを押すとスレッドがキャンセルされる仕組みとなっている。

`MainActivity`クラスは次のようにプログラミングした。

```kotlin
class MainActivity : AppCompatActivity() {

    var mWorker: Thread? = null
    var running = AtomicBoolean(false)
    var counter:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.start).setOnClickListener {
            runWorker()
        }
        findViewById<Button>(R.id.stop).setOnClickListener {
            interruptWorker()
        }
    }

    fun runWorker() {
        running.set(true)
        mWorker = Thread {
            while (running.get()) {
                try {
                    Thread.sleep(1000)
                    counter++
                    println("counter $counter")
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                    println("Thread was interrupted, Failed to complete operation")
                }

            }
        }
        mWorker?.start()
    }

    fun interruptWorker() {
        running.set(false)
        mWorker?.interrupt()
    }
}

```

## Treadの作成

スレッドの新規作成とそのスレッドをスタートさせる基本的な形は次のようになる。

```kotlin

mWorker = Thread { 
	...
	//処理内容
}
mWorker.start()
```


## Treadをキャンセルさせる

スレッドをキャンセルさせたい場合は`interrupt()`メソッドで行う。

```kotlin
mWorker?.interrupt()
```

しかしこのままだとスレッド処理内で例外が投げられクラッシュしてしまうだろう。
そこでスレッド内の処理を`try-catch`で囲んで上げる必要がある。さらに`catch`内では`Thread.currentThread().interrupt()`を再び実行することで安全に確実にスレッドをキャンセルさせている。

```kotlin
try {
	...
	//処理内容
} catch (ex: InterruptedException) {
	Thread.currentThread().interrupt()
}
```

## AtomicBoolean

`AtomicBoolean`は複数スレッドから安全に同時アクセスできるboolean型である。
ここではカウンターは`Int`型になっているが、厳密には`AtomicInteger`を利用すべきなのかもしれない。



## 参考

* https://www.baeldung.com/java-thread-stop
* https://discuss.kotlinlang.org/t/create-thread/7678
* https://ja.stackoverflow.com/questions/35667/%E3%82%AF%E3%83%A9%E3%82%B9-atomicboolean%E3%81%AB%E3%81%A4%E3%81%84%E3%81%A6

