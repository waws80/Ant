# 	<center>Ant网络请求框架

## 由来：
Ant网络访问框架的由来是因为每次写项目的时候都要引入第三方网络请求库和第三方图片请求库，每一个请求库都有自己的线程池。导致的问题就是线程不容易管理。而且还有就是使用不方便（不是说这些库不好，而是每次都要倒入好几个库麻烦）所以Ant网络请求框架就诞生了。

### 先来效果图
![](https://raw.githubusercontent.com/waws80/Ant/master/a.mp4)


#### 框架大概流程图
草图大概意思懂就行
![](https://github.com/waws80/Ant/blob/master/Antpic.png?raw=true)

### 框架技术
· 框架由kotlin所写
### 请求数据接口
· 框架的网络请求支持更换，用户可以根据自己的喜好使用HttpClient进行网络数据获取或者使用HttpUrlConnection都可以，只要实现对外暴露的Convert接口并将数据返回给回调接口即可。
### Https支持
· 用户可以使用默认的忽略证书式的默认DefaultSSLSocketFactory也可以自己去实现插入自己的证书，只需要请求的时候添加SSLSocketFactory即可。
### 网络获取数据支持缓存
· 网络缓存使用的SP进行本地数据缓存
### 图片获取
· 使用了将数据先获取到进行缓存（缓存到本地还是内存，取决于您，）然后将图片显示，如果使用过本地缓存使用的是libjpeg技术将图片进行压缩（图片在保真的情况下，占用内存大大减少，谁用谁知道
### 数据进度回调
· 支持实时获取当前数据的进度百分比（请求数据时需要后端配合），下载图片的时候只要添加回调接口就可以获取到下载进度（不支持缓存加载进度）
### 其他
· 支持Kotlin的DSL写法调用

## 用法

###获取网络数据
· `antString { 
            url = "https://www.baidu.com"
            /*methdo header body  ...*/
            complateString { 
                s -> log(s)
            }
       } `
### 获取网络图片
· `antImg {
            url = "https:////www.baidu.com/img/bd_logo1.png"
            //imageview
            target = iv1
            progress {
                p -> pb1.text = if (p == -1) "100%" else "$p%"
            }
            complate {
                toast("iv1 下载完成")
                pb1.visibility= View.GONE
            }
        }` 
              
       