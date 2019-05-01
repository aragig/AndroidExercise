![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226150003.png)


Androidの[公式ドキュメント](https://developer.android.com/guide/topics/ui/dialogs?hl=ja)よりダイアログの表示サンプルを試してみた。ドキュメントではJavaで記述されているがここではKotlinに書き直して実装した。





## ダイアログの基本

Androidでダイアログを表示させるには`Dialog`クラスを継承した`AlertDialog`を使う。`Activity`でインスタンス生成するのではなく、`DialogFragment`クラスを経由してインスタンスを生成する。`DialogFragment`クラスは`Fragment`を継承しているので`Activity`のライフサイクルと同期することができる。また、`DialogFragment`でカプセル化されるので`Activity`が肥大化せず、ダイアログを管理しやすくなる。

実際にサンプルを見たほうが分かりやすいので、次では`Cancel`と`Done`の2つのボタンを実装したシンプルなダイアログを作っていく。



## シンプルなダイアログ

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226133301.png)

`DialogFragment`クラスを継承した`SimpleDialogFragment`クラスを作成する。`onCreateDialog`メソッドをオーバーライドしてその中で`AlertDialog`のインスタンスをビルダーで生成している。

```kotlin
class SimpleDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            .setMessage("Here Message")
            .setPositiveButton("done") { dialog, id ->
                println("dialog:$dialog which:$id")
            }
            .setNegativeButton("cancel") { dialog, id ->
                println("dialog:$dialog which:$id")
            }

        return builder.create()
    }

}
```

`Activity`からは次のようにして呼び出すことができる。以降、ダイアログの呼び出しはすべてこの方法で行う。

```kotlin
val dialog = SimpleDialogFragment()
dialog.show(supportFragmentManager, "simple")
```




## リストメニューのダイアログ

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226133254.png)

先程の`シンプルなダイアログ`では`setPositiveButton`などでボタンを追加したが、リスト表示では`setItems`メソッドを使って配列をセットする。ここでは`setMessage`を実装してしまうとリストが表示されなくなるので注意したい。

```kotlin
class ListDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            // .setMessage("Here Message") // setMessageは使うとリスト表示されないので注意！
            .setItems(R.array.language_array) { dialog, which ->
                val langs = resources.getStringArray(R.array.language_array)
                println(langs[which])

            }

        return builder.create()
    }

}
```

`setItems`メソッドの第二引数へはクリックされたときの処理をラムダ式を渡すことになっている。このラムダ式は`OnClickListener`インタフェースで定義されている。ラムダ式内の第二引数(which)にはクリックされた配列のインデックスが渡される。

```kotlin
interface OnClickListener {
/**
* This method will be invoked when a button in the dialog is clicked.
*
* @param dialog the dialog that received the click
* @param which the button that was clicked (ex.
*              {@link DialogInterface#BUTTON_POSITIVE}) or the position
*              of the item clicked
*/
    void onClick(DialogInterface dialog, int which);
}
```




## チェックボックスのダイアログ

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226133249.png)

チェックボックスタイプのダイアログには`setMultiChoiceItems`を使う。`setMultiChoiceItems`の第二引数には、予めチェックしておきたい場所を`booleanArray型`で渡すことができる。


```kotlin

class CheckboxDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val checkedItems = booleanArrayOf(false, true ,false) // 保存されたデータに置き換えることができる
        val mSelectedItems:MutableList<Int> = mutableListOf()
        setupSelectedItems(checkedItems, mSelectedItems)


        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            // .setMessage("Here Message") // setMessageは使うとリスト表示されないので注意！
            .setMultiChoiceItems(R.array.language_array, checkedItems) { dialog, which, isChecked ->
                if (isChecked) {
                    mSelectedItems.add(which)
                } else {
                    mSelectedItems.remove(which)
                }
            }
            .setPositiveButton("OK") { dialog, id ->
                printSelectedStatus(mSelectedItems)
            }
            .setNegativeButton("Cancel") { dialog, id ->

            }


        return builder.create()
    }

    private fun setupSelectedItems(
        checkedItems: BooleanArray,
        mSelectedItems: MutableList<Int>
    ) {
        var index = 0
        checkedItems.forEach {
            if (it) {
                mSelectedItems.add(index)
            }
            index++
        }
    }

    private fun printSelectedStatus(mSelectedItems: MutableList<Int>) {
        val langs = resources.getStringArray(R.array.language_array)
        mSelectedItems.forEach {
            println(langs[it])
        }
    }
}
```



## ラジオボタンのダイアログ

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226133246.png)

ラジオボタンタイプのダイアログでは`setSingleChoiceItems`を使う。`setSingleChoiceItems`の第二引数にデフォルトでチェックしておきたいインデックスを`int型`で渡すことができる。

```kotlin
class RadiobuttonDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            .setSingleChoiceItems(R.array.language_array, 1) { dialog, which: Int ->
                println(which)
            }
            .setPositiveButton("OK") { dialog, id ->
            }
            .setNegativeButton("Cancel") { dialog, id ->

            }


        return builder.create()
    }
}
```

## カスタムレイアウトのダイアログ（ログイン入力）

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226133242.png)

ダイアログのレイアウトを自由にカスタマイズすることができる。ここでは公式ドキュメントにならってログイン入力用のダイアログを実装した。

まずは表示したいレイアウトファイル`dialog_signin.xml`を次のように作成する。

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools" android:orientation="vertical"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content">

    <EditText
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textEmailAddress"
            android:id="@+id/email"
            android:hint="Email"/>
    <EditText
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textPassword"
            android:id="@+id/password"
            android:hint="Password"/>
</LinearLayout>

```

そしてこのレイアウトを`DialogFragmentクラス`内で`inflater`で`inflate`する。つまり`dialog_signin`レイアウトを元にした`View`を生成する。丁寧にプログラムを追えば何も難しいことはない。

```kotlin

class SigninDialogFragment:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val signinView = inflater.inflate(R.layout.dialog_signin, null)

        builder.setView(signinView)
            .setTitle("Sign in")
            .setPositiveButton("OK") { dialog, id ->
                val email = signinView.findViewById<EditText>(R.id.email).text
                val password = signinView.findViewById<EditText>(R.id.password).text
                println("Email: $email Password:$password")
            }
            .setNegativeButton("Cancel") { dialog, id ->

            }

        return builder.create()
    }
}
```

## ダイアログからアクティビティへコールバックする

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226133238.png)

最後にアクティビティへコールバック可能なダイアログを作成する。インタフェースを使ってコールバックを実装することで`Activity`とダイアログを疎結合にすることができ、キレイなコードで管理することができる。ここでは`シンプルなダイアログ`のプログラムを元に改造した。

**NoticeDialogFragment.kt**

```kotlin
class NoticeDialogFragment: DialogFragment() {

    public interface NoticeDialogLister {
        public fun onDialogPositiveClick(dialog:DialogFragment)
        public fun onDialogNegativeClick(dialog:DialogFragment)
    }

    var mLister:NoticeDialogLister? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mLister = context as NoticeDialogLister
        } catch (e: ClassCastException) {
            throw ClassCastException("${context.toString()} must implement NoticeDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Here Title")
            .setMessage("Here Message")
            .setPositiveButton("ok") { dialog, id ->
                println("dialog:$dialog which:$id")
                mLister?.onDialogPositiveClick(this)
            }
            .setNegativeButton("cancel") { dialog, id ->
                println("dialog:$dialog which:$id")
                mLister?.onDialogNegativeClick(this)
            }

        return builder.create()
    }


    override fun onDestroy() {
        println("NoticeDialogFragmentのonDestroyが呼ばれたよ！")
        super.onDestroy()
    }

    override fun onDetach() {
        println("NoticeDialogFragmentのonDetachが呼ばれたよ！")
        super.onDetach()
	mLister = null

    }
}
```

上記のプログラムを詳しく見ていこう。

まず、コールバック用のインターフェース`NoticeDialogLister`を、`DialogFragment`を継承した`NoticeDialogFragment`クラス内に作成する。

**NoticeDialogFragment.kt**

```kotlin
class NoticeDialogFragment: DialogFragment() {

    public interface NoticeDialogLister {
        public fun onDialogPositiveClick(dialog:DialogFragment)
        public fun onDialogNegativeClick(dialog:DialogFragment)
    }
    ...
```

このインタフェースはダイアログを呼び出すホストつまり`MainActivity`へ実装することになる。

**MainActivity.kt**

```kotlin
class MainActivity : AppCompatActivity(), NoticeDialogFragment.NoticeDialogLister {

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        println("NoticeDialogでOKボタンが押されたよ！")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        println("NoticeDialogでCancelボタンが押されたよ！")
    }
	...
```

`onAttach`で`MainActivity`（ここではContextになっているが）を`NoticeDialogLister`へキャストしている。ホストの`Activity`に`NoticeDialogLister`インタフェースが実装されていなければ例外がスローされるようになっている。このことで実装のし忘れを避けることができるのだ。

**NoticeDialogFragment.kt**

```kotlin
	...
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mLister = context as NoticeDialogLister
        } catch (e: ClassCastException) {
            throw ClassCastException("${context.toString()} must implement NoticeDialogListener")
        }
    }
	...
```

そしてダイアログのボタンクリックイベント内で`NoticeDialogLister`のメソッドを実行しているのがわかるだろう。

**NoticeDialogFragment.kt**

```kotlin
...
.setPositiveButton("ok") { dialog, id ->
                println("dialog:$dialog which:$id")
                mLister?.onDialogPositiveClick(this)
            }
...
```

このコールバックの実装パターンは、iOS開発では頻繁に行われるデリゲートでのコールバックとよく似ている。Objective-C/Swiftの`protocol`がJava/Kotlinの`interface`に対応しているのだ。またiOSでは**強参照を避けるために`weak`を指定する**が、AndroidでのFragmentを通した実装ではその必要がないようである。ここは確かなことは言えないのだが、ダイアログを終了すると`DialogFragment`の`onDestroy`と`onDetach`が呼ばれることを確認しているので問題ないということにしたい。






## 英語雑学

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190226/20190226150003.png)

**Dialog / Dialogue**の語源は`log（話す）`から来ている。logはギリシャ語で「話す」や「単語」「会話」を意味する。`dia(間で)` + `log(話す)` であるのでつまりは**「対話」「意見交換」**といった意味になる。英英辞書では`(a) written conversation in a book or play`と書かれているように、本やゲームにおいての書かれた対話となる。

また`log`には`log house`というように**木**という意味もある。文字を木に書いていた歴史からもlogが対話（書かれた）などの意味をもつのは自然なことではないだろうか。


* The mayor tried to maintain a dialogue with the citizens.
* He's not very good at writing dialogue.


ちなみにlogを語源とする単語は他にもたくさん存在する。

* catalog(カタログ)
* logo(ロゴ)
* logic(論理)
* apology(謝罪)
* prologue(序幕)
* epilogue(結びの言葉)
* monologue(独白)

アプリのDialogもユーザーとの対話ということなので、一方的なスパムアラートにならないように表示のやり方には十分気を配りたいところである。




## 参考

* https://developer.android.com/guide/topics/ui/dialogs?hl=ja
* <a target="_blank" href="https://www.amazon.co.jp/gp/product/4327452319/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4327452319&linkCode=as2&tag=101010fun-22&linkId=6dc35d2b9376d4637299a934ca48c712">語根で覚える英単語 プラス ――語源によるサクサク英単語10倍記憶法</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4327452319" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />

<a target="_blank"  href="https://www.amazon.co.jp/gp/product/4798160199/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4798160199&linkCode=as2&tag=101010fun-22&linkId=aa197ba40d60bea04b960109f81d0593"><img border="0" src="//ws-fe.amazon-adsystem.com/widgets/q?_encoding=UTF8&MarketPlace=JP&ASIN=4798160199&ServiceVersion=20070822&ID=AsinImage&WS=1&Format=_SL250_&tag=101010fun-22" ></a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4798160199" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />
