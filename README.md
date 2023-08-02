# 簡易仕様書
## アプリ名：周辺グルメサーチ
## 対象OS(ver.含む)
Android 8.0以上
## 開発環境/言語
開発環境: Android Studio Flamingo | 2022.2.1 Patch 2  
言語: Kotlin 1.8.10
## 開発期間
約１週間
## 機能概要(機能一覧)
全体概要：現在地と半径を指定して範囲内の飲食店を検索
### 検索機能
ホットペッパー WebサービスのグルメサーチAPIを利用  
検索結果はリストで現在地から近い順に表示
- 検索条件
    - 現在地：GPSから現在地を取得
    - 検索範囲：現在地から範囲を指定（５択：300, 500, 1000, 2000, 3000m）

### 店舗詳細表示機能
検索結果の一覧から見たい店舗をタップするとその店舗の詳細が表示される  
詳細からGoogleMapを起動して店舗の場所を確認できる  
その他情報としてHOT PEPPERグルメサイトに飛べる

## 画面概要
### 検索条件入力画面
検索の条件を入力する  
現在地については検索を実行するタイミングで取得

### 検索結果画面
検索結果をリストで表示  
リストの１要素はボタンになっており，ボタン内に店舗の簡易情報が表示してある
- 店舗画像（看板等）
- 店舗名
- ジャンル
- アクセス

ボタンを押すと店舗詳細画面が表示される

### 店舗詳細画面
検索結果で選んだ店舗の詳細が表示される  
表示している情報
- 店舗名
- 店舗画像（外観，料理等　１枚）
- 住所
- 営業時間

GoogleMapを起動するボタンを用意し店舗の場所を確認できる  
HOT PEPPERグルメサイトに飛べるボタンを用意

今後実装すべき機能
- 簡単に店舗に電話できる機能  
ボタン等から電話を起動して店舗の電話番号が自動入力された状態までいける機能
- アプリ内で店舗までのルートを表示する機能

### ヘッダー部分
APIの利用規約でクレジット表示が必要なので常時表示

## 使用しているライブラリ、SDKなど
SDK：Android SDK

### ライブラリ
- androidx.core:core-ktx:1.8.0
- org.jetbrains.kotlin:kotlin-bom:1.8.0
- androidx.lifecycle:lifecycle-runtime-ktx:2.3.1
- androidx.activity:activity-compose:1.7.2
- androidx.compose:compose-bom:2022.10.00
- androidx.compose.ui:ui
- androidx.compose.ui:ui-graphics
- androidx.compose.ui:ui-tooling-preview
- androidx.compose.material3:material3
- androidx.compose.runtime:runtime-livedata:1.4.3
- androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha10
- androidx.fragment:fragment-ktx:1.6.0-alpha06
- androidx.appcompat:appcompat:1.6.1
- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4
- com.squareup.okhttp3:okhttp:4.10.0
- io.coil-kt:coil-compose:2.4.0
- com.google.android.gms:play-services-location:21.0.1

## コンセプト
### 周辺の店舗情報をさっと調べられる
周辺の店舗情報をさっと見られるようなデザインに．  
HOT PEPPERグルメという情報源があるのでアプリに載せる情報もある程度に抑えられる．  
他の情報は公式サイトに任せる．

## こだわったポイント
- MVVMアーキテクチャと依存性注入を意識して開発
- 検索範囲は選択式にして例外が発生しにくくした
- 検索結果リストは下端に近づいた際に追加の結果を取得させるように

## デザイン面でこだわったポイント
- 検索範囲指定をドロップダウンの選択式にして選択しやすいように
- 文字や間隔，画像サイズをある程度見やすいように調整
- できるだけスムーズな操作性に

## アドバイスして欲しいポイント
- Jetpack Composeの利用方法は今回の方法でいいのか  
Compose関数の作り方，画面遷移の方法（今回はFragmentを併用）　など
- テストコードを用意する上で直しておきたい部分

## 自己評価
短い開発期間でよくできた方だと思う．  
今回はじめてJetpack Composeを実践に近いコーディングで使用した．
勉強しつつだったので最低限の機能しか実装できなかったが，知識は得られたと思う．
確認できている不具合もまだあって期間内に対応できなかったのがとても悔しい．

## 確認できている不具合
- UIの状態保存がうまくいっておらず，アプリ起動中にカラーパータンを変更すると初期画面に戻ってしまう