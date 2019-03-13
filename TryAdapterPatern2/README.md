![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208213418.png)



[前回](https://www.101010.fun/entry/try-adapter-patern)の続きで、もう少しAdapterパターンで遊んでみたいと思う。
継承を使ったAdapterパターンで作った[「ドラえもんプログラム」](https://github.com/araemon/AndroidExercise/tree/master/TryAdapterPatern)をもとに、今度は委譲によるAdapterパターンに改造してみよう。


<a target="_blank"  href="https://www.amazon.co.jp/gp/product/4797327030/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4797327030&linkCode=as2&tag=101010fun-22&linkId=22211256466693978a5a408a835f9484"><img border="0" src="//ws-fe.amazon-adsystem.com/widgets/q?_encoding=UTF8&MarketPlace=JP&ASIN=4797327030&ServiceVersion=20070822&ID=AsinImage&WS=1&Format=_SL250_&tag=101010fun-22" ></a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4797327030" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />




## 委譲を使ったAdapterパターン

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208213422.png)

前回も書いたとおりAdapterパターンには継承と委譲の2つがあった。今までは継承だけをやってきたが、ここで委譲も試してみよう。

書き換えるのはHumanクラスとRequesterクラスの2つだけだ。

まず、Humanクラスを抽象クラスに書き換えよう。この様になる。
```kotlin
abstract  class Human {
    abstract fun wannaFly()
    abstract fun wannaGoAnywhere()
}
```

そしてRequesterクラスはHumanクラスを継承した具象クラスになる。
```kotlin
class Requester: Human() {
    ...
}
```

Requesterクラスの実装の中身はこの様になる。Doraemnインスタンスを生成し、doraemonフィールドに仕事をさせているのがわかるだろう。
```kotlin
val doraemon = Doraemon()

override fun wannaFly() {
    println(doraemon.fetchTakeKopter())
}

override fun wannaGoAnywhere() {
    println(doraemon.fetchDokodemoDoor())
}
```

もちろんClientによるTargetへの扱い方は、委譲のときも継承の場合と全く変わらない。
```kotlin
fun main() {
    val nobita:Human = Requester()
    nobita.wannaFly()
    nobita.wannaGoAnywhere()
}
```

## Doraemonがバージョンアップされた場合

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190208/20190208213418.png)


さて、Doraemonクラスがバージョンアップして、Doraemon2として提供されることになった。
新しいAPIはこんな感じである。列挙型を使って、道具を呼び出さなければならないようだ。

```kotlin
enum class ToolType {
    TAKEKOPTER {
        override fun message() = "タケコプター！！！🚁"
    },
    DOKODEMODOOR {
        override fun message() = "どこでもドア！！！🚪"
    }; // セミコロンを忘れずに！

    abstract fun message():String
}

open class Doraemon2 {

    fun fetchTools(tool:ToolType):String {
        return tool.message()
    }

}
```


慌てることはない。Adapterパターンを採用していたおかげで、最小限の修正で済みそうである。
Requesterクラスを、新バージョンのDoraemon2に対応するには、次のようになった。
```kotlin
class Requester: Human() {

    val doraemon = Doraemon2()

    override fun wannaFly() {
        println(doraemon.fetchTools(ToolType.TAKEKOPTER))
    }

    override fun wannaGoAnywhere() {
        println(doraemon.fetchTools(ToolType.DOKODEMODOOR))
    }
}
```

これでClient側のプログラム修正は行わなくて済んだ。



## まとめ
Client側では、Adapteeのクラスが古かろうが、新しかろうが知ったことではない。要求に答えてくれさえすれば良いのだ。このようなことを実現するのがAdapterの役割である。

プロジェクトの規模が大きくなった場合に、Adapterパターンは大活躍するであろう。導入時には少しややこしい部分があるが、メンテナンスなどにおいて他への影響が少なくて済む。

少しずつ使い方やメリットがわかってきたので、自分のプロジェクトにも意識的に活用してみたいと思う。



## 参考
* <a target="_blank" href="https://www.amazon.co.jp/gp/product/4865940391/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4865940391&linkCode=as2&tag=101010fun-22&linkId=9d21458e0a1f5e78f5983ed43f525f3d">Kotlinスタートブック -新しいAndroidプログラミング</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4865940391" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" /> (p.207 列挙型)
* <a target="_blank" href="https://www.amazon.co.jp/gp/product/4797327030/ref=as_li_tl?ie=UTF8&camp=247&creative=1211&creativeASIN=4797327030&linkCode=as2&tag=101010fun-22&linkId=92c236e37e598c6e1bfee7300726f770">増補改訂版Java言語で学ぶデザインパターン入門</a><img src="//ir-jp.amazon-adsystem.com/e/ir?t=101010fun-22&l=am2&o=9&a=4797327030" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" /> ( p20 委譲を使ったもの)
