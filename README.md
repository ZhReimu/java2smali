# java2smali

## SpringBoot 做的 java 转 smali 服务器

## 原理

1. 使用 Google 的 [d8](https://r8.googlesource.com/r8) 将 Java 转换成 dex
2. 使用 [baksmali](https://bitbucket.org/JesusFreke/smali/downloads/) 将 dex 转换成 smali

## libs 里的文件说明

d8* 来源于 (Google D8 仓库)[https://r8.googlesource.com/r8]
baksmali* 来源于 [baksmali 仓库](https://bitbucket.org/JesusFreke/smali/downloads/)
*.zip 是 源码, d8.jar 是基于官方仓库编译而来的, 因为 AndroidSdk 里的 d8.jar 经过了混淆, 不好用

### 使用方法

1. 克隆本仓库
2. 运行 SpringBoot 启动类
3. post http://localhost:8080/java2smali?clazzName=类名
4. postBody: key=file, content = java 文件
