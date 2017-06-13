package pw.androidthanatos.ant.controller

import pw.androidthanatos.ant.cache.ResponseCache
import java.io.InputStream

/**
 * 子线程获取网络数据回调接口，通过子类将数据发送给主线程对应的数据类型子类
 */
interface HttpListener {

    fun success(dataLength: Long,cacheText: String ,stream: InputStream,cache: Boolean, responseCache: ResponseCache,tag: String)

    fun getCache(text: String)

    fun error(errorCode: Int)
}