![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124743.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124746.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124749.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124753.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124756.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124800.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124804.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124807.jpg)

![](https://cdn-ak.f.st-hatena.com/images/fotolife/a/araemonz/20190311/20190311124811.jpg)


iOSアプリ開発でよくやるXcodeのターゲットを分ける方法をAndroidではビルドバリアント設定のFlavorsを使って行うことが出来る。ここでは調べた範囲でFlavorsの設定方法をまとめておく。




## 最終的なbuild.gradleの設定

```groovy
buildTypes {
	release {
		minifyEnabled false
		proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
	}
}
flavorDimensions "app"
productFlavors {
	demo {
		applicationIdSuffix ".demo"
		versionNameSuffix = "-demo"
		dimension "app"
	}
	full {
		applicationIdSuffix ".full"
		versionNameSuffix = "-full"
		dimension "app"
	}
}
```

## flavor dimensionでエラー


```
(ERROR: All flavors must now belong to a named flavor dimension. Learn more at https://d.android.com/r/tools/flavorDimensions-missing-error-message.html
Affected Modules: app
)
```

このエラーを解決するには次のように設定する。

```gradle
flavorDimensions "app"
productFlavors {
	demo {
		applicationIdSuffix ".demo"
		versionNameSuffix ="-demo"
		dimension "app"
	}
	full {
		applicationIdSuffix ".full"
		versionNameSuffix ="-full"
		dimension "app"
	}
}
```

`flavorDimensions` に仮に`app`という名前を設定した。これはグループ名のようなものらしい。
そしてそのグループに属するということを各Flavorに設定してあげる。
`dimension "app"`

`flavorDimensions`に設定されていない名前を使おうとすると、`Flavor 'full' has unknown dimension 'app2'.`のようにエラーが出てビルドできないので注意だ。

[イメージ図3]

`Android Studio`の左端に`Build Variants`メニューからこのようにビルドしたい`Build Variant`が表示される。`Product Flavor`と`Build Type` 掛け合わされて生成されているので`demoDebug`のような名前になっている。



ではそのまま`app2`を`flavorDimensions`に追加したらどうなるだろうか？

```
flavorDimensions "app", "app2"
productFlavors {
	demo {
		applicationIdSuffix ".demo"
		versionNameSuffix ="-demo"
		dimension "app"
	}
	full {
		applicationIdSuffix ".full"
		versionNameSuffix ="-full"
		dimension "app2"
	}
}
```

[イメージ図3]

すると次のように
`app` x `app2` x `Build Type` のように、`demoFullDebug`と`demoFullRelease`で生成されてしまった。これは意図していないので`app2`を元に戻そう




## Task sourceSetsの実行


```
10:34:21: Executing task 'sourceSets'...

Executing tasks: [sourceSets]


> Task :app:sourceSets

------------------------------------------------------------
Project :app
------------------------------------------------------------

androidTest
-----------
Compile configuration: androidTestCompile
build.gradle name: android.sourceSets.androidTest
Java sources: [app/src/androidTest/java]
Manifest file: app/src/androidTest/AndroidManifest.xml
Android resources: [app/src/androidTest/res]
Assets: [app/src/androidTest/assets]
AIDL sources: [app/src/androidTest/aidl]
RenderScript sources: [app/src/androidTest/rs]
JNI sources: [app/src/androidTest/jni]
JNI libraries: [app/src/androidTest/jniLibs]
Java-style resources: [app/src/androidTest/resources]

androidTestDebug
----------------
Compile configuration: androidTestDebugCompile
build.gradle name: android.sourceSets.androidTestDebug
Java sources: [app/src/androidTestDebug/java]
Manifest file: app/src/androidTestDebug/AndroidManifest.xml
Android resources: [app/src/androidTestDebug/res]
Assets: [app/src/androidTestDebug/assets]
AIDL sources: [app/src/androidTestDebug/aidl]
RenderScript sources: [app/src/androidTestDebug/rs]
JNI sources: [app/src/androidTestDebug/jni]
JNI libraries: [app/src/androidTestDebug/jniLibs]
Java-style resources: [app/src/androidTestDebug/resources]

androidTestDemo
---------------
Compile configuration: androidTestDemoCompile
build.gradle name: android.sourceSets.androidTestDemo
Java sources: [app/src/androidTestDemo/java]
Manifest file: app/src/androidTestDemo/AndroidManifest.xml
Android resources: [app/src/androidTestDemo/res]
Assets: [app/src/androidTestDemo/assets]
AIDL sources: [app/src/androidTestDemo/aidl]
RenderScript sources: [app/src/androidTestDemo/rs]
JNI sources: [app/src/androidTestDemo/jni]
JNI libraries: [app/src/androidTestDemo/jniLibs]
Java-style resources: [app/src/androidTestDemo/resources]

androidTestDemoDebug
--------------------
Compile configuration: androidTestDemoDebugCompile
build.gradle name: android.sourceSets.androidTestDemoDebug
Java sources: [app/src/androidTestDemoDebug/java]
Manifest file: app/src/androidTestDemoDebug/AndroidManifest.xml
Android resources: [app/src/androidTestDemoDebug/res]
Assets: [app/src/androidTestDemoDebug/assets]
AIDL sources: [app/src/androidTestDemoDebug/aidl]
RenderScript sources: [app/src/androidTestDemoDebug/rs]
JNI sources: [app/src/androidTestDemoDebug/jni]
JNI libraries: [app/src/androidTestDemoDebug/jniLibs]
Java-style resources: [app/src/androidTestDemoDebug/resources]

androidTestFull
---------------
Compile configuration: androidTestFullCompile
build.gradle name: android.sourceSets.androidTestFull
Java sources: [app/src/androidTestFull/java]
Manifest file: app/src/androidTestFull/AndroidManifest.xml
Android resources: [app/src/androidTestFull/res]
Assets: [app/src/androidTestFull/assets]
AIDL sources: [app/src/androidTestFull/aidl]
RenderScript sources: [app/src/androidTestFull/rs]
JNI sources: [app/src/androidTestFull/jni]
JNI libraries: [app/src/androidTestFull/jniLibs]
Java-style resources: [app/src/androidTestFull/resources]

androidTestFullDebug
--------------------
Compile configuration: androidTestFullDebugCompile
build.gradle name: android.sourceSets.androidTestFullDebug
Java sources: [app/src/androidTestFullDebug/java]
Manifest file: app/src/androidTestFullDebug/AndroidManifest.xml
Android resources: [app/src/androidTestFullDebug/res]
Assets: [app/src/androidTestFullDebug/assets]
AIDL sources: [app/src/androidTestFullDebug/aidl]
RenderScript sources: [app/src/androidTestFullDebug/rs]
JNI sources: [app/src/androidTestFullDebug/jni]
JNI libraries: [app/src/androidTestFullDebug/jniLibs]
Java-style resources: [app/src/androidTestFullDebug/resources]

debug
-----
Compile configuration: debugCompile
build.gradle name: android.sourceSets.debug
Java sources: [app/src/debug/java]
Manifest file: app/src/debug/AndroidManifest.xml
Android resources: [app/src/debug/res]
Assets: [app/src/debug/assets]
AIDL sources: [app/src/debug/aidl]
RenderScript sources: [app/src/debug/rs]
JNI sources: [app/src/debug/jni]
JNI libraries: [app/src/debug/jniLibs]
Java-style resources: [app/src/debug/resources]

demo
----
Compile configuration: demoCompile
build.gradle name: android.sourceSets.demo
Java sources: [app/src/demo/java]
Manifest file: app/src/demo/AndroidManifest.xml
Android resources: [app/src/demo/res]
Assets: [app/src/demo/assets]
AIDL sources: [app/src/demo/aidl]
RenderScript sources: [app/src/demo/rs]
JNI sources: [app/src/demo/jni]
JNI libraries: [app/src/demo/jniLibs]
Java-style resources: [app/src/demo/resources]

demoDebug
---------
Compile configuration: demoDebugCompile
build.gradle name: android.sourceSets.demoDebug
Java sources: [app/src/demoDebug/java]
Manifest file: app/src/demoDebug/AndroidManifest.xml
Android resources: [app/src/demoDebug/res]
Assets: [app/src/demoDebug/assets]
AIDL sources: [app/src/demoDebug/aidl]
RenderScript sources: [app/src/demoDebug/rs]
JNI sources: [app/src/demoDebug/jni]
JNI libraries: [app/src/demoDebug/jniLibs]
Java-style resources: [app/src/demoDebug/resources]

demoRelease
-----------
Compile configuration: demoReleaseCompile
build.gradle name: android.sourceSets.demoRelease
Java sources: [app/src/demoRelease/java]
Manifest file: app/src/demoRelease/AndroidManifest.xml
Android resources: [app/src/demoRelease/res]
Assets: [app/src/demoRelease/assets]
AIDL sources: [app/src/demoRelease/aidl]
RenderScript sources: [app/src/demoRelease/rs]
JNI sources: [app/src/demoRelease/jni]
JNI libraries: [app/src/demoRelease/jniLibs]
Java-style resources: [app/src/demoRelease/resources]

full
----
Compile configuration: fullCompile
build.gradle name: android.sourceSets.full
Java sources: [app/src/full/java]
Manifest file: app/src/full/AndroidManifest.xml
Android resources: [app/src/full/res]
Assets: [app/src/full/assets]
AIDL sources: [app/src/full/aidl]
RenderScript sources: [app/src/full/rs]
JNI sources: [app/src/full/jni]
JNI libraries: [app/src/full/jniLibs]
Java-style resources: [app/src/full/resources]

fullDebug
---------
Compile configuration: fullDebugCompile
build.gradle name: android.sourceSets.fullDebug
Java sources: [app/src/fullDebug/java]
Manifest file: app/src/fullDebug/AndroidManifest.xml
Android resources: [app/src/fullDebug/res]
Assets: [app/src/fullDebug/assets]
AIDL sources: [app/src/fullDebug/aidl]
RenderScript sources: [app/src/fullDebug/rs]
JNI sources: [app/src/fullDebug/jni]
JNI libraries: [app/src/fullDebug/jniLibs]
Java-style resources: [app/src/fullDebug/resources]

fullRelease
-----------
Compile configuration: fullReleaseCompile
build.gradle name: android.sourceSets.fullRelease
Java sources: [app/src/fullRelease/java]
Manifest file: app/src/fullRelease/AndroidManifest.xml
Android resources: [app/src/fullRelease/res]
Assets: [app/src/fullRelease/assets]
AIDL sources: [app/src/fullRelease/aidl]
RenderScript sources: [app/src/fullRelease/rs]
JNI sources: [app/src/fullRelease/jni]
JNI libraries: [app/src/fullRelease/jniLibs]
Java-style resources: [app/src/fullRelease/resources]

main
----
Compile configuration: compile
build.gradle name: android.sourceSets.main
Java sources: [app/src/main/java]
Manifest file: app/src/main/AndroidManifest.xml
Android resources: [app/src/main/res]
Assets: [app/src/main/assets]
AIDL sources: [app/src/main/aidl]
RenderScript sources: [app/src/main/rs]
JNI sources: [app/src/main/jni]
JNI libraries: [app/src/main/jniLibs]
Java-style resources: [app/src/main/resources]

release
-------
Compile configuration: releaseCompile
build.gradle name: android.sourceSets.release
Java sources: [app/src/release/java]
Manifest file: app/src/release/AndroidManifest.xml
Android resources: [app/src/release/res]
Assets: [app/src/release/assets]
AIDL sources: [app/src/release/aidl]
RenderScript sources: [app/src/release/rs]
JNI sources: [app/src/release/jni]
JNI libraries: [app/src/release/jniLibs]
Java-style resources: [app/src/release/resources]

test
----
Compile configuration: testCompile
build.gradle name: android.sourceSets.test
Java sources: [app/src/test/java]
Java-style resources: [app/src/test/resources]

testDebug
---------
Compile configuration: testDebugCompile
build.gradle name: android.sourceSets.testDebug
Java sources: [app/src/testDebug/java]
Java-style resources: [app/src/testDebug/resources]

testDemo
--------
Compile configuration: testDemoCompile
build.gradle name: android.sourceSets.testDemo
Java sources: [app/src/testDemo/java]
Java-style resources: [app/src/testDemo/resources]

testDemoDebug
-------------
Compile configuration: testDemoDebugCompile
build.gradle name: android.sourceSets.testDemoDebug
Java sources: [app/src/testDemoDebug/java]
Java-style resources: [app/src/testDemoDebug/resources]

testDemoRelease
---------------
Compile configuration: testDemoReleaseCompile
build.gradle name: android.sourceSets.testDemoRelease
Java sources: [app/src/testDemoRelease/java]
Java-style resources: [app/src/testDemoRelease/resources]

testFull
--------
Compile configuration: testFullCompile
build.gradle name: android.sourceSets.testFull
Java sources: [app/src/testFull/java]
Java-style resources: [app/src/testFull/resources]

testFullDebug
-------------
Compile configuration: testFullDebugCompile
build.gradle name: android.sourceSets.testFullDebug
Java sources: [app/src/testFullDebug/java]
Java-style resources: [app/src/testFullDebug/resources]

testFullRelease
---------------
Compile configuration: testFullReleaseCompile
build.gradle name: android.sourceSets.testFullRelease
Java sources: [app/src/testFullRelease/java]
Java-style resources: [app/src/testFullRelease/resources]

testRelease
-----------
Compile configuration: testReleaseCompile
build.gradle name: android.sourceSets.testRelease
Java sources: [app/src/testRelease/java]
Java-style resources: [app/src/testRelease/resources]


BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
10:34:21: Task execution finished 'sourceSets'.
```


## Javaフォルダーの色が青色にならない


[stack overflow](https://stackoverflow.com/questions/21697565/java-source-directories-for-android-studio-project-flavors-not-rendered-in-blue)に同様の質問と解決法があった。
これはBuild Variantを切り替えればブルーに表示されるようだ。
親切にも現在対象となるJavaフォルダのみを青色に表示してくれているということだ。



## 問題点


Flaverを使うと複雑になっていく。

* テストの問題
* マニフェストの問題
* 依存関係の管理
* MainActivityの問題


素直に別プロジェクトに分けたほうが良い場合もあるので注意して使いたい。リソースだけを変えてリリースするようなアプリ開発には向いている。


