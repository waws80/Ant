package pw.androidthanatos.ant.controller

import pw.androidthanatos.ant.cache.ResponseCache
import java.io.InputStream

/**
 * 子线程获取网络数据回调接口，通过子类将数据发送给主线程对应的数据类型子类
 */
interface HttpListener {

    /**
     * 网络获取数据成功回调
     * @param dataLength 数据的总长度 通过响应头获取
     * @param cacheText 缓存的文本，没有传空字符串
     * @param stream 网络获取的io流
     * @param cache 是否启用cache
     * @param responseCache 缓存对象
     * @param tag url标识   使用系统方法 eg： getCacheKey(chain.url,chain.method,chain.headers)
     */
    fun success(dataLength: Long,cacheText: String ,stream: InputStream,cache: Boolean, responseCache: ResponseCache,tag: String)

    fun getCache(text: String)

    fun error(errorCode: Int)
}