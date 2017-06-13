package pw.androidthanatos.ant.controller

import android.content.Context
import android.support.annotation.NonNull
import pw.androidthanatos.ant.convert.Convert
import javax.net.ssl.SSLSocketFactory

/**
 * 网络请求服务接口
 */
interface HttpService {

    /**
     * 添加url
     * @param url 访问网络使用的链接
     */
    fun addUrl(@NonNull url: String)

    /**
     * 网络请求的方式
     * @param method eg: AntMethod.GET
     */
    fun addMethod(@NonNull method: AntMethod)

    /**
     * 设置是否启用缓存
     */
    fun setCache(cache: Boolean)

    /**
     * 设置上下文
     */
    fun setContext(context: Context?)

    /**
     * 访问网络所需要的请求头数据
     * @param headers 请求头的数据，数据是 HashMap
     */
    fun addHeader(@NonNull headers: HashMap<String,String>)

    /**
     * 访问网络请求体中的数据
     * @param body 向请求体中存放的数据
     */
    fun addBody(@NonNull body: String)

    /**
     * 支持https请求所需的证书
     */
    fun addSSLSocketFactory(@NonNull socketFactory: SSLSocketFactory)

    /**
     * 访问网络之前，判断是否需要拦截此次请求
     */
    fun addInterceptor(@NonNull interceptor: AntInterceptor)

    /**
     * 添加网络请求 eg: HttpUrlConnection ..
     */
    fun addConvert(@NonNull convert: Convert)

    /**
     * 添加网络获取数据结果回调
     */
    fun addHttpCallBack(@NonNull listener: HttpListener)

    /**
     *  开始执行网络操作
     */
    fun execute()

}