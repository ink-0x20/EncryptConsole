# EncryptConsole [![license](https://img.shields.io/badge/license-MIT-green.svg?style=flat-square)](https://github.com/ink-0x20/EncryptConsole/blob/master/LICENSE)
[English](https://translate.google.com/translate?sl=ja&tl=en&u=https://github.com/ink-0x20/EncryptConsole) (by Google Translate)  
コンソール上でファイルを暗号化するツールです。  
ファイルの暗号化のみの対応で、フォルダ等の暗号化には対応していません。  
ツール等で使用することを目的としているため、GUIは対応していません。  
また、このツールを使用して生じた損害等は、一切の責任を負いかねますのであらかじめご了承ください。

### 特徴
パスワードを0～2個設定できるようにしています。  
もちろん、パスワードは1つ以上設定することを推奨します。  
※パスワードなしでもは暗号化はしていますが、復元される可能性が高いので自己責任でお願いします

### 暗号化方式
| 内容 | 設定値 |
|:-----------|:------------|
| アルゴリズム | AES256 |
| ブロックモード | CBC |
| パディング方式 | PKCS5Padding |

### オプション一覧
| オプション | オプション(詳細) | 引数 | 内容 |
|:-----------|:------------|:------------|:------------|
| -e | --encrypt | - | 暗号化モードで起動します |
| -d | --decrypt | - | 復号モードで起動します |
| -del | --delete | - | 元のファイルを削除します |
| -ext | --extension | 拡張子名 | 暗号化後のファイル拡張子を指定します※省略した場合は.logになります |
| -p | --password1 | 第一パスワード | 暗号/復号時の第一パスワードを指定します※省略可 |
| -pp | --password2 | 第二パスワード | 暗号/復号時の第二パスワードを指定します（第一パスワードを指定したときのみ指定可能※省略可） |
| -f | --file | 暗号化ファイルパス | 暗号化する対象のファイルパスを入力します |

## 開発環境
Eclipse 2021  
Java16,17

### 使用ライブラリ
Apache Comons Codec  
Apache Comons Lang  
Apache Comons Cli

## LICENSE
[MIT](https://github.com/ink-0x20/EncryptConsole/blob/master/LICENSE)
