


動画のように画面に指を触れて動かしたときの移動量を取得できるようなプログラムを作ってみた。


![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190306/20190306135449.gif)




## TouchableFragmentクラス

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190306/20190306132633.jpg)


今回はフラグメントを使用してタッチイベントのフックを行った。
`TouchableFragment`クラスを作成し、フラグメントのレイアウトファイルにイベントフック専用のビューである`touchableView`を配置した。
このビューをフラグメントの`onCreateView`で取得しリスナー登録を済ませておく。


```kotlin
override fun onCreateView(
	inflater: LayoutInflater, container: ViewGroup?,
	savedInstanceState: Bundle?
): View? {

	val view = inflater.inflate(R.layout.fragment_touchable, container, false)
	view.findViewById<View>(R.id.touchableView).setOnTouchListener(this)
	return view
}
```



次に`TouchableFragment`クラスに`View.OnTouchListener`インタフェースを実装する。そしてそのメンバメソッドである`onTouch`メソッドを次のようにオーバーライドする。

```kotlin
private var yPrec = 0.0f

override fun onTouch(v: View?, event: MotionEvent?): Boolean {
	when (event?.actionMasked) {
		MotionEvent.ACTION_DOWN -> {
			yPrec = event.getY(0)
			listener?.actionDown()
		}
		MotionEvent.ACTION_MOVE -> {
			val dy = yPrec - event.getY(0)
			listener?.actionMove(dy)
		}
		MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
			listener?.actionUp()
		}
	}
	return true
}
```

今回は移動量を計算させているが他にも`VelocityTracker`を使って指の速度を得ることも出来るようになっている。詳しくはこちらを参考にしてもらいたい。

[Track touch and pointer movements](https://developer.android.com/training/gestures/movement?hl=ja)


さて`TouchableFragment`クラスの全体のプログラム内容は次のようになっている。

```kotlin

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TouchableFragment : Fragment(),View.OnTouchListener {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnTouchableFragmentListener? = null

    private var yPrec = 0.0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                yPrec = event.getY(0)
                listener?.actionDown()
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = yPrec - event.getY(0)
                listener?.actionMove(dy)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                listener?.actionUp()
            }
        }
        return true
    }

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

        val view = inflater.inflate(R.layout.fragment_touchable, container, false)
        view.findViewById<View>(R.id.touchableView).setOnTouchListener(this)
        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTouchableFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnTouchableFragmentListener {
        fun actionDown()
        fun actionMove(dy: Float)
        fun actionUp()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TouchableFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
```


## MainActivityクラス

<figure class="figure-image figure-image-fotolife" title="activity_main.xml">[f:id:araemonz:20190306133003j:plain]<figcaption>activity_main.xml</figcaption></figure>

次に`MainActivity`クラスでの実装を行っていく。アクティビティのレイアウトファイルの左半分にはテキストビューを設置し、右半分にはフラグメントを入れ込むためにフレームレイアウトを設置した。

そして`MainActivity`から動的にフラグメントを生成させるためのコードは次のようになる。

```kotlin
private const val TOUCHABLE_FRAGMENT_TAG = "touchableFragment"

class MainActivity : AppCompatActivity(), TouchableFragment.OnTouchableFragmentListener {


    override fun actionDown() {
        @SuppressLint("SetTextI18n")
        resultTextView.text = "Action Down\n${resultTextView.text}"
    }

    override fun actionMove(dy: Float) {
        @SuppressLint("SetTextI18n")
        resultTextView.text = "dy: $dy\n" +
                "${resultTextView.text}"
    }

    override fun actionUp() {
        @SuppressLint("SetTextI18n")
        resultTextView.text = "\nAction Up\n" +
                "${resultTextView.text}"
    }

    lateinit var resultTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result)

        if (supportFragmentManager.findFragmentByTag(TOUCHABLE_FRAGMENT_TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, TouchableFragment.newInstance("hoge", "fuga"), TOUCHABLE_FRAGMENT_TAG)
                .commit()
        }
    }


}
```

フラグメントをファクトリメソッドで生成し引数を渡せるようにしている。またフラグメントからのコールバックを受けて取れるようにインターフェースを使った実装を行っている。先頭の`actionDown`、`actionMove`、`actionUp`メソッドは`TouchableFragment`からのコールバック関数となる。`actionMove`では指の移動量が渡されるのでその値をテキストビューにセットして表示させている。


最後にプロジェクトを実行し、画面右半分の中で指を動かせばテキストビューに移動量が表示されるだろう。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190306/20190306133003.jpg)

タッチイベントの処理をアクティビティに直接書いてしまうとアクティビティが読みづらくなってしまうので、このようにフラグメントに追い出した方が管理しやすくなるので参考にしたい。



## 参考

* [Track touch and pointer movements](https://developer.android.com/training/gestures/movement?hl=ja)
* [android: move a view on touch move (ACTION_MOVE)](https://stackoverflow.com/questions/9398057/android-move-a-view-on-touch-move-action-move)
