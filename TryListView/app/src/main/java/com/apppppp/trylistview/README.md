# ListViewを簡単なモデルで理解しよう | Android Kotlin


## 完成イメージ

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207173822.png)

標準に搭載されているタイムゾーンの配列を`TimeZone.getAvailableIDs()`で取得して、一覧表示するという単純な仕様だ。
APIなど複雑な処理はないので、リストビューの理解だけに集中できるようになっている。




## ListViewのレイアウトファイルを作る
配置したListViewの行のレイアウトファイルを作りたい場合は、ListViewをクリックして、listitemの項目の`...`をクリック。開いたダイアログの右上から、`New layout File`を選択して作成すると簡単だ。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207173818.jpg)

こんな感じにレイアウトする。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207173814.jpg)


リストの行のタップ領域高さは、48dp以上が推奨されているようだ。あまりに狭いと、ユーザビリティが悪くなるので気をつけよう。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207172908.png)




## 基本のListView
BaseAdapterクラスを継承した、TimeZoneAdapterクラスを作成する。

```kotlin
class TimeZoneAdapter(private val context: Context,
                      private val timeZones: Array<String> = TimeZone.getAvailableIDs())
    : BaseAdapter() {


    private val inflater = LayoutInflater.from(context)

    // インデックスp0にある行のビューを返す
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.list_time_zone_row, parent, false)
        val timeZoneId = getItem(position)
        val timeZone = TimeZone.getTimeZone(timeZoneId)

        val timeZoneLabel = view.findViewById<TextView>(R.id.timeZone)
        timeZoneLabel.text = "${timeZone.displayName}(${timeZone.id})"

        val clock = view.findViewById<TextClock>(R.id.clock)
        clock.timeZone = timeZone.id

        return view
    }

    // インデックスp0にあるデータを返す
    override fun getItem(position: Int) = timeZones[position]

    // 行を識別するためのユニーク値
    override fun getItemId(position: Int) = position.toLong()

    // リスト表示するデータ件数
    override fun getCount() = timeZones.size

}
```



getViewの部分が一番混乱するところだろう。次のようなことをやっているのがgetViewだ。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207172915.jpg)

getViewは、positionに指定された行のViewを返す。Viewが生成されていないならinflaterで生成し、すでに存在すれば、そのViewを使い回す。また、実際の値をリストに設定する処理もここで書く。





## ViewHolderで速度改善
実は先程のプログラムは、次の部分に問題がある。
```kotlin
val timeZoneLabel = view.findViewById<TextView>(R.id.timeZone)
```
スクロールするたびに、findViewByIdを呼び出すのはコストがかかるのだ。つまりスクロール動作が遅くなる。そこで登場するのがViewHolderパターンだ。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207172911.png)

Viewにはtagプロパティというのがあり、そこに任意のオブジェクトを1つ持つことができる。このことを利用して、予めtimeZoneLabelなどのインスタンスを生成して持たせておくのだ。
convertViewはリサイクルされるので、tagに持たせたオブジェクトも再利用されるはずである。




そして次のコードが、 ViewHolderパターンに書き換えたものだ。

```kotlin
class TimeZoneAdapter(private val context: Context,
                      private val timeZones: Array<String> = TimeZone.getAvailableIDs())
    : BaseAdapter() {


    private val inflater = LayoutInflater.from(context)

    // インデックスp0にある行のビューを返す
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: createView(parent)
        val timeZoneId = getItem(position)
        val timeZone = TimeZone.getTimeZone(timeZoneId)

        val viewHolder = view.tag as ViewHolder

        @SuppressLint("SetTextI18n")
        viewHolder.name.text = "${timeZone.displayName}(${timeZone.id})"
        viewHolder.clock.timeZone = timeZone.id

        return view
    }

    // インデックスp0にあるデータを返す
    override fun getItem(position: Int) = timeZones[position]

    // 行を識別するためのユニーク値
    override fun getItemId(position: Int) = position.toLong()

    // リスト表示するデータ件数
    override fun getCount() = timeZones.size


    private fun createView(parent: ViewGroup?) : View {
        val view = inflater.inflate(R.layout.list_time_zone_row, parent, false)
        view.tag = ViewHolder(view)
        return view
    }



    private class ViewHolder(view: View) {
        val name = view.findViewById<TextView>(R.id.timeZone)
        val clock = view.findViewById<TextClock>(R.id.clock)
    }

}
```

ListViewの動作が重いと思ったら、ここを見直してみよう。





## MainActivityにListViewを実装する
最後に、アクティビティにListView実装しよう。

```kotlin
package com.apppppp.trylistview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val list = findViewById<ListView>(R.id.clockList)
        val adapter = TimeZoneAdapter(this)

        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->

            val timeZone = adapter.getItem(position)
            println(timeZone)
        }

    }
}

```

これで完成だ。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207173822.png)




## createView が呼び出される回数を確認
createViewが何回呼び出されるか確認するために、カウンターを追加して測定してみた。Viewがリサイクルされるならば、カウンターの値は一定値以上にはならないはずだ。

```kotlin
private var count = 0

private fun createView(parent: ViewGroup?) : View {
    println(count++)

    val view = inflater.inflate(R.layout.list_time_zone_row, parent, false)
    view.tag = ViewHolder(view)
    return view
}
```

リストをスクロールすると11回まで呼び出され、それ以上は呼び出されなかった。これで、Viewがリサイクルされていることが確認できた。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190207/20190207173810.jpg)






## 参考
<a target="_blank" href="https://www.amazon.co.jp/gp/product/479739580X/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=479739580X&linkCode=as2&tag=101010fun-22&linkId=7780fb8251672fd12a4b940b0266376b">基本からしっかり身につくAndroidアプリ開発入門 Android Studio 3対応 (「黒帯エンジニア」シリーズ)</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=479739580X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />

