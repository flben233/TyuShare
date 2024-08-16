# 小雨妙享
## 安装
下载[Release](https://github.com/flben233/TyuShare/release)中的安装包进行安装即可

### Native编译环境

| 名称 | 版本 |
| - | - |
GraalVM | GraalVM CE 22.0.2+9.1
Visual Studio | 2022

### 更新提示

若更新时修改了安装路径，请删除原安装目录下的旧版本，若更新前设置了开机自启，请重设一次 (关了再开)，否则会导致**音频串流端口占用**的问题

## 已知问题
1. 因为compose-desktop打包的限制，在系统中这个软件的名字叫做 `TyuShare`
2. 在两台不同分辨率的显示器之间拖动程序窗口可能会出现布局错乱
3. 多显示器设备键鼠共享鼠标可能跑出窗口

## TODO
1. 文件拖拽发送

## 使用

框内输入目标电脑 IP，点击连接。或者点击旁边的历史连接记录也可以连接。

![](https://res.shirakawatyu.top/c171fd96d35a4232bb6abade4d103253.png)

## 使用的开源项目
[compose-multiplatform](https://github.com/JetBrains/compose-multiplatform)

[flatlaf](https://github.com/JFormDesigner/FlatLaf)

[hutool](https://github.com/dromara/hutool)

[kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization)

[oshi](https://github.com/oshi/oshi)

[mpfilepicker](https://github.com/Wavesonics/compose-multiplatform-file-picker)

[jnativehook](https://github.com/kwhat/jnativehook)
