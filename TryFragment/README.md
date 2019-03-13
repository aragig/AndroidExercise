![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190302/20190302154720.png)

図のように2つの`Fragment`を1つの`Activity`で操作する簡単なサンプルプロジェクトを作ってみたのでまとめておく。



[:contents]


## activity_main.xmlを用意する

まず`Fragment`を動的に追加するためのコンテナとして、空っぽの状態のレイアウトファイルを用意する。今回は1つだけしか`Activity`を使わないので`activity_main.xml`を修正することにした。`ConstraintLayout`の`id`を`container`としていることに注意しよう。

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity" android:id="@+id/container">
</android.support.constraint.ConstraintLayout>
```



## ActivityからFragmentを呼び出す

`Activity`の`onCreate`で最初に表示したい`Fragment`を設定するには次のようなコードを書くことになる。

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
	super.onCreate(savedInstanceState)
	setContentView(R.layout.activity_main)

	val hogeFragment = HogeFragment()
	val transaction = supportFragmentManager.beginTransaction()
	transaction.add(R.id.container, hogeFragment)
	transaction.commit()
}
```

ここでは`HogeFragment`クラスを用意することにした。`HogeFragment`クラスの内容は次のとおりである。

```kotlin
class HogeFragment : Fragment() {

    private var listener: OnHogeFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_hoge, container, false)
        val addFragment = fragmentView.findViewById<Button>(R.id.addFragment)
        addFragment.setOnClickListener {
            addFragment()
        }
        return fragmentView
    }

    fun addFragment() {
        listener?.onHogeFragmentAddFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHogeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHogeFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnHogeFragmentListener {
        fun onHogeFragmentAddFragment()
    }

}
```



`HogeFragment`クラスでは、[以前の記事](https://www.101010.fun/entry/android-onbutton-click)のように、レイアウトファイルの`onClick`属性にメソッドを実装したかったのだが、実際にボタンを押すと次のようなエラーが出てしまった。

```
java.lang.IllegalStateException: Could not find method onButtonPressed(View) in a parent or ancestor Context for android:onClick attribute defined on view class android.support.v7.widget.AppCompatButton with id 'button'
        at android.support.v7.app.AppCompatViewInflater$DeclaredOnClickListener.resolveMethod(AppCompatViewInflater.java:424)
    
```

どうも`HogeFragment`クラス内のメソッドを見つけてくれないようだ。仕方がないので`setOnClickListener`でクラスに直接書き込むことにした。

```kotlin
override fun onCreateView(
	inflater: LayoutInflater, container: ViewGroup?,
	savedInstanceState: Bundle?
): View? {
	val fragmentView = inflater.inflate(R.layout.fragment_hoge, container, false)

	val addFragment = fragmentView.findViewById<Button>(R.id.addFragment)
	addFragment.setOnClickListener {
		addFragment()
	}
	return fragmentView
}
```

このように`inflater`で生成した`View`から`findViewById`でボタンの参照を取得する。そして`setOnClickListener`でボタンのクリックイベントを設定した。


## Fragment内のViewにアクセスしたい

ところで`Fragment`内の`TextView`にアクセスしたい場合はどうすればよいだろうか？
最良の方法かどうかはわからないが、次のようにして`View`の参照を保持することで可能になった。考え方は先程のボタンイベントの設定のときと同じだ。

```kotlin
private var fugaText:TextView? = null

override fun onCreateView(
	inflater: LayoutInflater, container: ViewGroup?,
	savedInstanceState: Bundle?
): View? {
	val view = inflater.inflate(R.layout.fragment_fuga, container, false)
	fugaText = view.findViewById(R.id.fugaText)
	...
}
```

この`TextView`を実装したのが次の`FugaFragment`クラスである。

```kotlin
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FugaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFugaFragmentListener? = null
    private var fugaText:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fuga, container, false)

        fugaText = view.findViewById(R.id.fugaText)


        val displayParams = view.findViewById<Button>(R.id.displayParams)
        displayParams.setOnClickListener {
            displayParams()
        }

        val finishButton = view.findViewById<Button>(R.id.finishButton)
        finishButton.setOnClickListener {
            finish()
        }
        return view
    }

    fun displayParams() {
        fugaText?.text = "$ARG_PARAM1: $param1 / $ARG_PARAM2: $param2"
    }

    fun finish() {
        listener?.onHugaFragmentFinish()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFugaFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFugaFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        println("FugaFragment onDetach")
    }


    interface OnFugaFragmentListener {
        fun onHugaFragmentFinish()
    }

    companion object {
        /**
         * ファクトリーメソッド(シングルトンではない)
         * newInstanceを呼び出してインスタンス生成することで引数を渡せる
         * */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FugaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
```



## ActivityからFragmentへ値を渡す

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190302/20190302150203.jpg)

この`FugaFragment`には`companion object`というシングルトンで使うような宣言がある。しかしこれはシングルトンではなくファクトリーメソッドであることに注意しよう。
`Android Studio`で`Fragment`を新規作成するときに、`include fragment factory methods?`にチェックを入れることでこのようにファクトリーメソッドが自動生成されるのだ。

```kotlin
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FugaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

	...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

	...
	companion object {
		/**
			* ファクトリーメソッド(シングルトンではない)
			* newInstanceを呼び出してインスタンス生成することで引数を渡せる
			* */
		@JvmStatic
		fun newInstance(param1: String, param2: String) =
			FugaFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_PARAM1, param1)
					putString(ARG_PARAM2, param2)
				}
			}
	}
}
```

ファクトリーメソッドを使って`Activity`から`Fragment`を生成してみよう。
```kotlin
val hugaFragment = FugaFragment.newInstance("りんご", "バナナ")
```
このようにファクトリーメソッドを使うことで`Fragment`は`Activity`からパラメータを受けてることが出来る。


## Fragmentの切り替えとバックスタックへ追加

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190302/20190302153423.png)

`FugeFragment`のボタンを押すと`Activity`へ`onFugeFragmentAddFragment`通知が届くようになっている。`onFugeFragmentAddFragment`メソッドの中で`Fragment`を`FugaFragment`に切り替えてみることにしよう。次のように記述することで`Fragment`を切り替えることができる。

```kotlin
override fun onHogeFragmentAddFragment() {
	val hugaFragment = FugaFragment.newInstance("りんご", "バナナ")
	val transaction = supportFragmentManager.beginTransaction()
	transaction.replace(R.id.container, hugaFragment)
	transaction.addToBackStack(null) // バックスタックに保存する。呼び出さなければ積まれない。
	transaction.commit()
}
```

`transaction.addToBackStack(null)`の一行に注目しよう。この処理を書くことで、1つ前の`HogeFragment`はバックスタックに積まれることになる。バックスタックに積んだことによって、`FugaFragment`から1つ前の`HogeFragment`へ戻ることが可能となるのだ。また端末の戻るボタンからも1つ前の`Fragment`へ戻ることができる。もしも戻ることを許可したくない場合には`addToBackStack`の一行を書かなければ良い。



ところで`Activity`と`Fragment`のやり取りはインタフェースを介してやりとりしているが、[ダイアログ](https://www.101010.fun/entry/android-dialog-kotlin)や[ネットワーク](https://www.101010.fun/entry/fragment-asynctask-networking)のサンプルでも説明してきたので今回は詳細は省略する。


## 1つ前のFragmentへ戻る

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190302/20190302153438.png)

戻るときは簡単で、積んである`BackStack`から（Popする）取り出せばよい。
```kotlin
override fun onHugaFragmentFinish() {
	supportFragmentManager.popBackStack()
}
```

`BackStack`からPopすると現在の表示されている`Fragment`は`onDetach`が呼ばれ開放される。


## まとめ

`Fragment`は`Activity`のようなライフサイクルをもつ事ができるクラスだ。
`View`レイアウトをもつことも出来るが、[ネットワークサンプル](https://www.101010.fun/entry/fragment-asynctask-networking)で試したときのように`View`をまったく持たない`Fragment`も作ることが出来た。このことは`Fragment`単位で様々な処理を分担できることを意味しており、つまり`Activity`からの制御を行いやすくすることができる。したがって重要な大まかな流れだけを`Activity`に書くことで、格段にメンテナンスがしやすくなるのだ。


今回のサンプルもまたGitHubへプッシュしておいたので参考に。

[TryFragment](https://github.com/araemon/AndroidExercise/tree/master/TryFragment)



## 参考

* https://developer.android.com/guide/components/fragments?hl=ja
* https://qiita.com/dmnlk/items/786840c7d910e59b81dc
* http://appera.hatenablog.com/entry/2016/09/06/135218
* http://umegusa.hatenablog.jp/entry/2014/03/31/004515
* https://rakuishi.com/archives/6637/

