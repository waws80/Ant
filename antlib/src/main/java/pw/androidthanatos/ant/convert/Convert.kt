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
     */
    fun httpConvert(isNetWork: Boolean,cache: Boolean,responseCache: ResponseCache,chain: Chain,listener: HttpListener)

    /**
     * 网络请求执行体 https
     * @param chain 网络请求数据体
     * @param listener 网络请求数据回调函数
     */
    fun httpsConvert(isNetWork: Boolean ,socketFactory: SSLSocketFactory, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener)
}