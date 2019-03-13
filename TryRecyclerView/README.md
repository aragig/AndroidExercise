今回のサンプルプロジェクトではこちらの本の内容を元に、リサイクラービューを使って上図のような正方形のリストアイテムの表示を行ってみた。


![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304112810.png)



## リサイクラービューとは

リストビューよりも柔軟性の高い表現ができるビューのことである。リスト内にカードやカルーセルを並べたり、横方向にスクロールさせたり、リストアイテムをグリッド状に並べたりすることができる。

リサイクラービューのレイアウトマネージャーには次の3つが用意されている。

1. LinearLayoutManager
2. GridLayoutManager
3. StaggeredGridLayoutManager

`LinearLayoutManager`はリストビューと似ていて、アイテムを縦に並べる事ができるが横にも並べることが出来るものだ。`GridLayoutManager`はグリッド状にリストアイテムを並べることができる。今回のサンプルプログラムではこの`GridLayoutMnager`を使ったリサイクラービューを実装することにする。最後の`StaggeredGridLayoutManager`は`GridLayoutManager`をさらに柔軟にしたものと言えよう。`Staggered`とは「ジグザグの」と言う意味で、`GridLayoutManager`では普通、行の高さが揃うことになっているがこのレイアウトでは行の高さを別々にすることが出来るのだ。

これからリサイクラービューを使う上で注意しておきたいのは、リサイクラービューはサポートライブラリーに含まれるということだ。サポートライブラリとは古いOSでも使用できるように後方互換を考慮したライブラリだ。リサイクラービューはバージョン7に含まれるのでGradleでは次のように追加されることになる。

`implementation 'com.android.support:recyclerview-v7:28.0.0'`

## Adapterクラスの作成

それではリサイクラービューを作っていこう。
リストビューの時と違って`BaseAdapter`ではなく`RecyclerView`に定義されている`Adapter`クラスを継承して実装する。
またリストビューと同様にViewHolderパターンを使用する。

```kotlin
class SampleAdapter(context: Context, private val onItemClicked: (TimeZone) -> Unit):
        RecyclerView.Adapter<SampleAdapter.SampleViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val timeZones = TimeZone.getAvailableIDs().map {
        id -> TimeZone.getTimeZone(id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val view = inflater.inflate(R.layout.list_time_zone_row, parent, false)
        val viewHolder = SampleViewHolder(view)

        view.setOnClickListener {
            val position = viewHolder.adapterPosition
            val timeZone = timeZones[position]
            onItemClicked(timeZone)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = timeZones.size

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        val timeZone = timeZones[position]
        holder.timeZone.text = timeZone.id
    }


    class SampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeZone = view.findViewById<TextView>(R.id.timeZone)
    }

}
```

リサイクラービューで実装すべきメソッドは次の3つである。

1. getItemCount():Int
2. onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
3. onBindViewHolder(holder: SampleViewHolder, position: Int)

それではこれらを一つ一つ見ていこう。

#### getItemCount():Int
ここではリストアイテムの件数を返すことになっている。リストビューの時とは異なり、ヘッダーやフッターもカウントの対象になることに注意しよう。今回はヘッダーもフッターも表示しないのでTimeZoneで得られた配列のサイズをそのまま返すことになっている。

#### onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
ここでは表示するリストアイテムのViewをinflaterで生成する。ViewHolderパターンを使うので、`RecyclerView.ViewHolder`を継承した`SampleViewHolder`というクラスを作りそれを返すことにした。またリストアイテムをクリックしたときのイベントハンドラの登録もここで行うようにしよう。

#### onBindViewHolder(holder: ViewHolder, position: Int)
ここでは`onCreateViewHolder`で生成した`ViewHolder`が渡されるので、表示させたい値をここで設定しよう。


## MainActivityでRecyclerViewの設定

`RecyclerView`のプログラムが出来たところで、`MainActivity`から`RecyclerView`を呼び出す設定をしよう。

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        
        val sampleAdapter = SampleAdapter(this) { timeZone ->
            Toast.makeText(this, timeZone.displayName, Toast.LENGTH_SHORT).show()
        }
        val gridManager = GridLayoutManager(this, 4)

        val recyclerView = findViewById<RecyclerView>(R.id.timeZoneList). apply {
            adapter = sampleAdapter
            layoutManager = gridManager
        }

    }
}
```

今回は`SampleAdapter`クラスのコンストラクタの第二引数に、ラムダ式を渡す仕様となっている。ラムダ式内にリストアイテムがクリックされたときの処理を書き込もう。
`GridLayoutManager`の第二引数には1行に配置したいリストアイテムの数を指定することができる。

以上のプログラムを`Build&Run`すると次のようなリサイクルビューを表示することができた。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304112816.png)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304094916.png)



## 正方形のマスを画面サイズに合わせてキレイに配置する

ところで正方形のリストアイテムをグリッドで並べたい場合にはどうすればよいだろうか？

まず`list_time_zone_row.xml`の`TextView`のサイズを次のように固定にしてしまおう。

```xml
android:layout_width="80dp"
android:layout_height="80dp"
```

しかし画面が回転したりして横幅が大きくなってしまうと、無駄にスペースが空いてしまう。
そこで画面の現在の横幅(dp)を取得して何個リストアイテムを入れられるか計算させることにしよう。
`onCreate`メソッド内を次のように修正した。

```kotlin
val screenWidthDp = resources.configuration.screenWidthDp // 追記
val spanCount:Int = screenWidthDp / 88 // 追記

val gridManager = GridLayoutManager(this, spanCount) // spanCountに修正
```

すると次のように画面サイズに合わせてキレイに配置することができた。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190304/20190304105227.png)


以上でリサイクラービューの基本的な使い方の説明を終わる。
今回のサンプルはGitHubへ公開しておく。

[AndroidExercise/TryRecyclerView](https://github.com/araemon/AndroidExercise/tree/master/TryRecyclerView)



## 参考

* <a target="_blank" href="https://www.amazon.co.jp/gp/product/479739580X/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=479739580X&linkCode=as2&tag=101010fun-22&linkId=a90b9f4d5dfb0c1555b81a0d4cfd413a">基本からしっかり身につくAndroidアプリ開発入門 Android Studio 3対応 (「黒帯エンジニア」シリーズ)</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=479739580X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />
* [https://developer.android.com/guide/topics/ui/layout/recyclerview](https://developer.android.com/guide/topics/ui/layout/recyclerview)
