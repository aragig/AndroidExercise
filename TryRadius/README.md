![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190305/20190305124127.jpg)


## 角丸表現

レイアウトを角丸で表現するには`drawable`にソースファイルを作って行うことになる。

まず`res`の`drawable`ディレクトリに`shape_rounded_corners_10dp.xml`という名前で`drawable resource file`を新規作成する。そして内容は次のように書き込んだ。

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <corners android:radius="10dp"/>
    <solid android:color="@color/colorPrimary"/>
    <stroke android:color="@color/colorPrimaryDark"
            android:width="4dp" />
</shape>
```

`LinearLayout`や`Button`、`TextView`などの`background`属性に先程のリソースファイルを指定する。このように`android:background="@drawable/shape_rounded_corners_10dp"`を指定してあげれば、はじめの図のような角丸表現ができる。



## タイトルバーを非表示にする

余談だがタイトルバーの非表示設定はマニフェストのthemeを次のように修正すると可能となる。

```
android:theme="@style/Theme.AppCompat.Light.NoActionBar"
```




## 参考

* [https://developer.android.com/guide/topics/resources/drawable-resource.html#Shape](https://developer.android.com/guide/topics/resources/drawable-resource.html#Shape)
* [https://qiita.com/furu8ma/items/4328c793250b10313cd7](https://qiita.com/furu8ma/items/4328c793250b10313cd7)

