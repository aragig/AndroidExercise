![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190206/20190206013615.jpg)

DataBindingを試すにあたって、MVVMデザインパータンについても調べてみたのでまとめてみる。



## MVVM デザインパターン

あまり考えずにボタンやテーブルを追加したりしていくと、いつの間にか肥大化しているController。メンテナンスなどしたくないと思っているのは、私だけでは無いはず。

そんな悩みを解決する方法の1つがMVVMというデザインモデルだ。
MVVM とは Model、View、ViewModelというデザインパターンである。

### View とは
**画面の描画とユーザー入力の受付**

ViewだけでなくViewController、ActivityもViewとして扱う。
描画に関するロジックのみを持ち、ユーザーからの入力はViewModelに任せる。



### ViewModel とは
**Viewから渡されたイベントを受け付ける役割**

Viewから渡されたボタンタップなどのイベントをここで実際に処理していく。
Viewに表示するデータの提供もここで行う。
これらのことをプレゼーションロジックと呼ぶ。



### Model とは
**UIに依存しないロジック部分**

残りの、UIに依存しないロジックはModelに実装する。
表現したいデータをAPIから取得したり、保存したいデーターをデーターベースへ書き込んだり、要求されたデータに答える。
これらのことはビジネスロジックと呼ばれている。



## バインディング

このMVVMは、バインディングという機能で実現する。バインディングとは、結びつけるという意味で、例えばTextViewに文字をセットする際、TextView.text = "hoge"直接、または変数を使って代入していたものを、TextView.text = viewModel.hoge などという形で、Viewモデルを介して代入するのである。Viewモデルで値を更新すると、TextView.textの値も自動で更新してくれるのだ。これらの仕組みを提供してくれるのがバインディングと呼ばれる機能である。オブザーバーパターンとも呼べる。



## DataBindingを実装する

`DataBinding`の使い方を学ぶため、簡単なサンプルを作って試してみた。

簡単に手順を説明する。




### 手順1
こんな感じでTextViewとButtonを用意する。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190206/20190206005705.jpg)


### 手順2
build.gradle (Module: app)に、以下のように追記する。

```build.gradle
apply plugin: 'kotlin-kapt'// ここを追記

android {
	...
	// ここを追記
    dataBinding {
        enabled = true
    }
}
```

### 手順3
 viewmodelパッケージを作成。（パッケージ名に大文字を使うとアクセスできなくなるので注意！）その中に、MainViewModel.ktファイルを作成する。ファイル内容は次の通り。

```kotlin
class MainViewModel {
    var message = ObservableField("こんにちは、世界！")
    fun onClickButton(v:View) {
        Log.d("mopi", "$v")
        message.set("Successfully, DataBinding!")
    }
}
```

### 手順4
activity_main.xmlを編集


![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190206/20190206004525.png)
  

### 手順5
最後にMainActivity.ktに、おまじないの追加
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // おまじないの追加
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = MainViewModel()
    }
}
```

### ビルド
ビルドし、実行する。ボタンをクリックすると中央のテキストが変わることがわかる。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190206/20190206014257.png)

役割分担がはっきりして、とてもいい感じだ。また、一度設定してしまえば、Viewの更新を気にすることなくプログラミングできるのは素晴らしい。積極的にDataBindingを使ってみたいと思った。


## 参考

* [https://qiita.com/funeasy-soft/items/ab5fc3f8f770a91adc25:title]
*  [https://qiita.com/SYABU555/items/3ca6f43135e79c0fa8ca:title]

