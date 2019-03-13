![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304132636.jpg)

今回は下記の書籍のサンプルを元にフラグメントを2つ作って連携させ、さらに`onSaveInstanceState`を使ったアクティビティの状態の復元を行った。


## ButtonFragment
　
`ButtonFragment`を次のようにして作成した。

```kotlin
class ButtonFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_button, container, false)
        view.findViewById<Button>(R.id.button)
            .setOnClickListener {
                listener?.onButtonClicked()
            }

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



    interface OnFragmentInteractionListener {
        fun onButtonClicked()
    }

}
```

`ButtonFragment`は`fragment`に一つだけ`Button`をもつ。クリックイベントはインタフェースを介してホストアクティビティへ通知できるようにしている。


## TextFragment

`TextFragment`は`ButtonFragment`でボタンがクリックされる毎にカウントアップを表示させるためのフラグメントだ。また、カウンターはファクトリーメソッドを使ってホストアクティビティから初期値を設定できるようにしている。

```kotlin
private const val COUNTER_KEY = "counter"

class TextFragment : Fragment() {

    private var mCounter = 0
    private lateinit var counterTextView: TextView

    fun update() {
        mCounter++
        counterTextView.text = mCounter.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCounter = it.getInt(COUNTER_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_text, container, false)
        counterTextView = view.findViewById(R.id.textView)
        counterTextView.text = mCounter.toString()
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(counter: Int) =
            TextFragment().apply {
                arguments = Bundle().apply {
                    putInt(COUNTER_KEY, counter)
                }
            }
    }
}

```

`update()`メソッドは後ほど`MainActivity`から呼び出すことになる。


## Fragmentをレイアウトファイルに設置する

今回は`ButtonFragment`を直接レイアウトファイルに設置し、`TextFragment`は`MainActivity`から動的に設置する。

まず`ButtonFragment`を設置しよう。`Palette`から`fragment`を探してドラッグアンドドロップする。

```xml
<fragment
		android:layout_width="0dp"
		android:layout_height="0dp" android:name="com.apppppp.trysavedinstancestate.ButtonFragment"
		android:id="@+id/buttonFragment" android:layout_marginTop="8dp"
		app:layout_constraintTop_toTopOf="@+id/guideline2" app:layout_constraintStart_toStartOf="parent"
		android:layout_marginStart="8dp" app:layout_constraintEnd_toEndOf="parent" android:layout_marginEnd="8dp"
		android:layout_marginBottom="8dp" app:layout_constraintBottom_toBottomOf="parent"
		tools:layout="@layout/fragment_button"/>
```

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304123757.jpg)

図のようにlayout属性に`@layout/fragment_button`を忘れずに指定しよう。これを設定しないとコンパイルエラーとなってしまうのだ。


つぎに`TextFragment`を動的に追加するため`FrameLayout`を使ったコンテナを作成する。

```xml
<FrameLayout
		android:layout_width="0dp"
		android:layout_height="0dp" app:layout_constraintTop_toTopOf="parent"
		android:layout_marginTop="8dp" app:layout_constraintStart_toStartOf="parent"
		android:layout_marginStart="8dp" android:layout_marginEnd="8dp" app:layout_constraintEnd_toEndOf="parent"
		android:id="@+id/container" android:layout_marginBottom="8dp"
		app:layout_constraintBottom_toTopOf="@+id/buttonFragment">

</FrameLayout>
```


このコンテナに動的に`TextFragment`を追加するプログラムを見てみよう。`MainActivity`を次のように記述する。

```kotlin
private const val TEXT_FRAGMENT_TAG = "textFragment"

class MainActivity : AppCompatActivity(), ButtonFragment.OnFragmentInteractionListener {

    override fun onButtonClicked() {
        val fragment = supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG) as TextFragment
        fragment.update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, TextFragment.newInstance(100), TEXT_FRAGMENT_TAG)
                .commit()
        }
    }
}

```

注目すべきところは`fragment`にTAGをつけて`supportFragmentManager.findFragmentByTag`のように、どこからでも参照可能なようにしている。もし返り値がnullであるならば、まだフラグメントが生成されていないということなので、トランザクションを使用してフラグメントを新規作成している。


```kotlin
override fun onButtonClicked() {
	val fragment = supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG) as TextFragment
	fragment.update()
}
```



## 状態の保存

ここまでのプログラムを実際に実行してみよう。カウンターの初期値に100が表示され、ボタンをクリックする毎に1づつカウントアップされるのが確認できることだろう。

しかし画面を回転するとどうだろうか？カウントアップの表示が初期化されてしまった。


![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304124810.png)

実は画面が回転されるとAndroidシステムがレイアウトの再ロードを行う必要があるために、アクティビティが破棄され再作成される仕様となっているのだ。このことはこちらに詳しく書かれているので参照したい。

[アクティビティの再作成](https://developer.android.com/training/basics/activity-lifecycle/recreating?hl=ja)

そこで解決策の1つとして`onSaveInstanceState()`コールバックメソッドを使った状態の保存を実装することにする。`onSaveInstanceState()`はアクティビティが破棄される前に呼び出されるメソッドだ。そのメソッド内で`Bundle`オブジェクトを使った状態の保存を行う。保存した値は`onCreate()`または`onRestoreInstanceState()`で受け取ることができる。


今回はカウンターの値を保存しておきたいので、`TextFragment`に次のように`onSaveInstanceState()`を実装した。

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
	outState.putInt(COUNTER_KEY, mCounter)
	super.onSaveInstanceState(outState)
}
```

そして`onCreate`メソッドを次のように書き換えた。
```kotlin
mCounter = savedInstanceState?.getInt(COUNTER_KEY)
			?: arguments?.getInt(COUNTER_KEY)
			?: 0

```
エルビス演算子を使って`savedInstanceState`に値がなければ`arguments`から値を呼び出し、それでも値がなければカウンターに初期値として`0`を設定している。

この状態でプログラムを再度実行してみよう。ボタンを何回か押してカウントアップさせたあと画面を回転してみるとどうなるだろうか？見事カウントの値が保持されたまま再表示されることが確認できた。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304130127.png)



## まとめ

最後にこの記事で以下の内容を行ってきたことをおさらいしておこう。

* **フラグメントからイベントをホストアクティビティに通知した。**
* **ファクトリーメソッドを使ってフラグメントへ初期値を渡した。**
* **フラグメントをTAGを使って管理した。**
* **アクティビティからフラグメントを動的に追加した。**
* **`onSaveInstanceState`を使って画面回転時における状態の保存と復元を行った。**



## 参考

* <a target="_blank" href="https://www.amazon.co.jp/gp/product/479739580X/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=479739580X&linkCode=as2&tag=101010fun-22&linkId=a90b9f4d5dfb0c1555b81a0d4cfd413a">基本からしっかり身につくAndroidアプリ開発入門 Android Studio 3対応 (「黒帯エンジニア」シリーズ)</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=479739580X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />
* [アクティビティの再作成](https://developer.android.com/training/basics/activity-lifecycle/recreating?hl=ja)
