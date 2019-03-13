
2017年に追加された`SpringAnimation`を使って簡単なバネアニメーションを実践してみた。

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311024737.gif)



## ライブラリの追加

`SpringAnimation`を使うためにはライブラリを追加する必要がある。`build.gradle(Module: app)`の`dependencies`に以下の一行を追記する。

```kotlin
implementation 'com.android.support:support-dynamic-animation:28.0.0'
```

## アニメーションするプログラム


今回は`SpringAnimation`を使ってViewのスケールを変化させてみる。アニメーションのプログラム内容は以下のようになった。

```kotlin
fun doSpringAnimation(sender: View) {

	sender.scaleX = 0.5f
	sender.scaleY = 0.5f

	val (anim1X, anim1Y ) = sender.let { view ->
		SpringAnimation(view, DynamicAnimation.SCALE_X, 1.0f ) to
				SpringAnimation(view, DynamicAnimation.SCALE_Y, 1.0f)
	}
	anim1X.spring.apply {
		dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
		stiffness = SpringForce.STIFFNESS_LOW
	}
	anim1Y.spring.apply {
		dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
		stiffness = SpringForce.STIFFNESS_LOW
	}
	anim1X.start()
	anim1Y.start()
}
```


上記のメソッドを使う場合は次のようにしてアニメーションさせたいViewを渡せばよい。

```kotlin
val target = findViewById<View>(R.id.imageView)
doSpringAnimation(target)
```




## 参考
* [https://www.youtube.com/embed/BNcODK-Ju0g](https://www.youtube.com/embed/BNcODK-Ju0g)
* [Animate movement using spring physics](https://developer.android.com/guide/topics/graphics/spring-animation)
* [Android Animations Spring to Life (Google I/O '17)](https://www.youtube.com/watch?v=BNcODK-Ju0g)
* [新しく公開された Spring Animation を使う](https://qiita.com/takusemba/items/2d52fb5fc5505228f49b)
