package pw.androidthanatos.ant.convert

import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.controller.HttpListener
import pw.androidthanatos.ant.entity.Chain
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocketFactory

/**
 * 网络请求插件接口，供外界使用
 */
interface Convert {

    /**
     * 网络请求执行体 http
     * @param chain 网络请求数据体
     * @param listener 网络请求数据回调函数
     * @param isNetWork 当前设备是否有网络
     * @param cache 是否使用缓存 true 使用缓存
     * @param responseCache  缓存对象 使用此对象将数据加入到缓存
     */
    fun httpConvert(isNetWork: Boolean,cache: Boolean,responseCache: ResponseCache,chain: Chain,listener: HttpListener)

    /**
     * 网络请求执行体 https
     * @param chain 网络请求数据体
     * @param listener 网络请求数据回调函数
     * @param isNetWork 当前设备是否有网络
     * @param cache 是否使用缓存 true 使用缓存
     * @param responseCache  缓存对象 使用此对象将数据加入到缓存
     */
    fun httpsConvert(isNetWork: Boolean ,socketFactory: SSLSocketFactory, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener)
}