package pw.androidthanatos.ant.controller

import android.content.Context
import android.util.Base64
import pw.androidthanatos.ant.Ant
import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.convert.Convert
import pw.androidthanatos.ant.entity.Chain
import pw.androidthanatos.ant.util.getCacheKey
import pw.androidthanatos.ant.util.isNetWorkAvailable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.TreeSet
import javax.net.ssl.SSLSocketFactory

/**
 * 网络服务类，对用户的数据进行拼装发送给网络请求插件 convert
 */
class HttpServiceImpl: HttpService {

    /*访问网络的路径地址*/
    private lateinit var url: String

    /*访问网络的方法*/
    private var method: AntMethod = AntMethod.GET

    /*访问网络的请求头*/
    private lateinit var headers: HashMap<String, String>

    /*访问网络的请求体*/
    private lateinit var body: String

    /*支持https的证书*/
    private var socketFactory: SSLSocketFactory? = null

    /*访问网络的拦截器*/
    private var interceptor: AntInterceptor? = null

    /*添加网络请求方式*/
    private lateinit var convert: Convert

    /*添加网络获取结果回调*/
    private var listener: HttpListener? = null

    private var cache: Boolean = false

    private var context: Context? =null




    override fun addUrl(url: String) {
        this.url = url
    }


    override fun addMethod(method: AntMethod) {
        this.method = method
    }

    override fun setCache(cache: Boolean) {
        this.cache = cache
        if (cache){

        }
    }

    override fun setContext(context: Context?) {
        this.context = context
    }


    override fun addHeader(headers: HashMap<String, String>) {
        this.headers = headers
    }


    override fun addBody(body: String) {
        this.body = body
    }


    override fun addSSLSocketFactory(socketFactory: SSLSocketFactory) {
        this.socketFactory = socketFactory
    }


    override fun addInterceptor(interceptor: AntInterceptor) {
        this.interceptor = interceptor
    }

    override fun addConvert(convert: Convert) {
        this.convert = convert
    }

    override fun addHttpCallBack(listener: HttpListener) {
        this.listener =listener
    }


    override fun execute() {
        if (context == null) throw  RuntimeException("请初始化Ant")
        val responseCache = ResponseCache(context!!)
        if (!url.trim().startsWith("http")){
            throw IllegalArgumentException("请求的url错误")
        }
        val chain = Chain(url, method, headers, body)
        if (listener == null) throw RuntimeException("网络请求回调接口为空,请先添加网络请求回调接口")
        if (interceptor != null ){
            val n_chain = interceptor!!.interceptor(chain)
            logInfo(n_chain)
            if (n_chain.url.startsWith("https")){
                Ant.antLog("带证书请求")
                val a = returnCache(cache,n_chain,listener!!,responseCache)
                if (!a){
                    Ant.antLog("带证书网络请求")
                    convert.httpsConvert(isNetWorkAvailable(context!!),socketFactory!!,cache,responseCache,n_chain,listener!!)
                }

            }else{
                val a =returnCache(cache,n_chain,listener!!,responseCache)
                if(!a){
                    convert.httpConvert(isNetWorkAvailable(context!!),cache,responseCache,n_chain,listener!!)
                }

            }

        }else{
            logInfo(chain)

            if (chain.url.startsWith("https")){
                Ant.antLog("带证书请求")
                val a =returnCache(cache,chain,listener!!,responseCache)
                if (!a){
                    Ant.antLog("带证书网络请求")
                    convert.httpsConvert(isNetWorkAvailable(context!!),socketFactory!!,cache,responseCache,chain,listener!!)
                }

            }else{
                val a =returnCache(cache,chain,listener!!,responseCache)
                if (!a){
                    Ant.antLog("直接网络获取")
                    convert.httpConvert(isNetWorkAvailable(context!!),cache,responseCache,chain,listener!!)
                }

            }
        }


    }

    fun returnCache(cache: Boolean,chain: Chain,listener: HttpListener,responseCache: ResponseCache): Boolean{
        if (cache){
            val text: String = responseCache.get(getCacheKey(chain.url,chain.method,chain.headers))
            if (text.isNotEmpty()){
                val arr = text.replace("[","").replace("]","").split(",")
                val bytearray = Base64.decode(arr[1].toByteArray(Charset.forName("UTF-8")),Base64.DEFAULT)
                val text = String(bytearray, Charset.forName("UTF-8"))
                Ant.antLog("获取到了缓存$text")
                listener.getCache(text)
                return true
            }

        }
        return false
    }


    fun logInfo(chain: Chain){

        val head = StringBuffer()
        chain.headers.forEach {
            head.append("  key:  ${it.key}  value:  ${it.value} \n")
        }
        Ant.antLog("------------------------------\n请求开始：\n请求的url：${chain.url}\n请求的方式：${chain.method}\n" +
                "请求头：$head  \n请求体：${chain.body} \n")
    }
}