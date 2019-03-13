![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208193347.png)


<a target=“_blank" href="https://www.amazon.co.jp/gp/product/4797327030/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4797327030&linkCode=as2&tag=101010fun-22&linkId=92c236e37e598c6e1bfee7300726f770">増補改訂版Java言語で学ぶデザインパターン入門</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4797327030" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" /> を読み始めた。ここ数ヶ月で学びたかったことが何だったのか、ようやく見えてきた気がする。つまり私はデザインパターンを学びたかったのだ。

色々なプログラミングの本をあさってはみてきたものの、そのプログラミングがどうしてそういう書き方になっているのかイマイチ理解できなかった。それもそのはず、デザインパターンというのを理解していなかったからだろう。本書では23ものデザインパターンを紹介しているので、一つ一つゆっくり理解しながら読み進めて行きたいと思う。

今回は第2章の、**Adapterパターン**について触れてみたいと思う。なお、プログラミング言語はKotlinで、IntelliJで実行している。

 [:contents]

## Adapter パターンとは

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208175444.png)

Adapterパターンというのを、物質世界でたとえてみよう。


100V電源につなぐアダプターをイメージしてほしい。例えばUSBアダプター。100Vを5Vに変換するものだ。5Vの先にはスマホや、モバイルバッテリー、はたまたLEDライトなどと今ではたくさんの機器がUSBアダプターへ接続可能となっている。
言い換えれば、すでにある100V電圧を、アダプターを通して5Vに変換して利用できるようにするということだ。



利用する側は**Client**と呼び、この場合だと、人間そのものになるだろう。そして、人間はスマホを始めとした機器をつかう。これらの機器はClientである人間に対して、**Target**と呼ばれる。Targetは、すでに家庭に供給されている100V電源をなんとかして使いたい。そこで**Adapter**の登場だ。ここではUSBアダプターがまさにAdapter役だ。また、すでに提供されている100V電源のようなものを**Adaptee**と呼ぶ。






## クラスによるAdapter パターン

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208175348.png)

Adapterパターンを、クラスの関係に当てはめてみよう。

**Adaptee**は既に提供されているクラスとすることができる。
**Target**はAdapteeを、なんとかして利用したいと思っているクラスだ。
その仲介役を担うクラスが、**Adapter**になる。
**Client**はMainだったり、Activityだったり、Targetを呼び出すクラスとなるだろう。







## 「ドラえもん」で、Adapter パターンの実践

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208193347.png)

先程のクラスの関係をもとに、Adapterパターンを実践してみよう。


Adaptee役にDoraemonクラスを作ってみた。既に提供されているものとして扱うことにする。つまり、Doraemonクラスは固定されたものとして扱うのだ。

```kotlin
open class Doraemon {
    
    fun fetchTakeKopter():String {
        return "タケコプター！！！🚁"
    }

    fun fetchDokodemoDoor():String {
        return "どこでもドア！！！🚪"
    }
}
```



Doraemonクラスのメソッドをなんとか呼び出したいTargetが、つぎのHumanだ。ここではインタフェースとして、次のようなメソッドを定義している。

```kotlin
interface Human {
    fun wannaFly()
    fun wannaGoAnywhere()
}
```


当然このままではHumanとDoraemonは互換性がない。

そこでAdapter役として互換性を持たせるために、Requesterクラスを作ってみよう。このクラスはDoraemonクラスを継承し、Humanインタフェースを実装している。

```kotlin
class Requester: Doraemon(), Human {
    override fun wannaFly() {
        println(fetchTakeKopter())
    }

    override fun wannaGoAnywhere() {
        println(fetchDokodemoDoor())
    }
}
```
実装は単純で、Doraemonクラスから得られた文字列を、println関数でそのまま標準出力するだけである。



それでは、最後にmain関数を実装し、Client側でこれらを扱ってみよう。

```kotlin
fun main() {
    val nobita:Human = Requester()
    nobita.wannaFly()
    nobita.wannaGoAnywhere()
}
```

実行結果はこの様になる。
```
タケコプター！！！🚁
どこでもドア！！！🚪
```



main関数の中で注目すべきところは次の部分だ。

```kotlin
val nobita:Human = Requester()
```
**nobitaオブジェクトはあくまでHumanインタフェース型であって、Requester型ではない**というところだ。このことは、**Doraemonクラスに存在する、`fetchTakeKopter`や`fetchDokodemoDoor`などの実装をClientに隠すことができる。**

また、既存クラスの上に一枚クラスをかぶせるので**Wrapperパターン**とも呼ばれるようだ。


## なぜAdapter パターンなのか

Adapterなんてややこしいものを作らず、直接Doraemonインスタンスを生成してメソッドを呼び出せば良いのでは？私も最初はそう思った。



Humanクラスを作成して、そこにDoraemonクラスをもたせることになるだろうか。しかしその場合、HumanクラスはいちいちDoraemonクラスのメソッドを知っていなければならない。それに、機能を変更したい場合に、DoraemonクラスとHumanクラスとの線引が不明確にならないだろうか？



一方、Adapterパターンを使えば、それらは一目瞭然。DoraemonクラスとHumanクラスが互いに関係しているのは、Requestクラスのみとなる。今後、Adapterを入れ替えたり、機能追加することも簡単だ。

つまりはAdapterパターンを使うということで、メンテナンスがしやすくなったり、クラスの再利用が可能になったりするのだ。**Adapterパターンは既存のクラスを全く変えることがなく、目的のインタフェース（API）に合わせることができる**ために生まれるメリットがあることを覚えておこう。



ところでストーリー的に考えてみても、のび太がドラえもんの道具を直接、取り出すことができてしまうのはおかしいことなので、Adapterパターンによる呼び出しがしっくりくるのではないだろうか。



最後にひとつ。実は、Adapterパターンには二種類存在する。ひとつは**継承**によるパターン。もう一つは**委譲**によるパターンがある。今回の場合は前者の継承によるAdapterパターンを使ったことになることを付け加えておきたい。



## 参考
<a target="_blank"  href="https://www.amazon.co.jp/gp/product/4797327030/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4797327030&linkCode=as2&tag=101010fun-22&linkId=22211256466693978a5a408a835f9484"><img border="0" src="//ws-fe.amazon-adsystem.com/widgets/q?_encoding=UTF8&MarketPlace=JP&ASIN=4797327030&ServiceVersion=20070822&ID=AsinImage&WS=1&Format=_SL250_&tag=101010fun-22" ></a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4797327030" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />

