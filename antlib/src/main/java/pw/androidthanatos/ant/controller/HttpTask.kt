package pw.androidthanatos.ant.controller

import android.content.Context
import org.json.JSONObject
import pw.androidthanatos.ant.convert.Convert
import pw.androidthanatos.ant.data.JSONListener
import pw.androidthanatos.ant.data.StringListener
import pw.androidthanatos.ant.img.ByteArrayListener
import javax.net.ssl.SSLSocketFactory

/**
 * 网络请求体
 */
class HttpTask {

    private var url: String = ""

    private var method: AntMethod = AntMethod.GET

    private var headers: HashMap<String,String> = HashMap()

    private var body: String = ""

    private var socketFactory: SSLSocketFactory? = null

    private lateinit var convert: Convert


    private var interceptor: AntInterceptor? = null

    private var cache: Boolean = false

    private var context: Context? = null


    fun addUrl(url: String) {
        this.url = url
    }


    fun addMethod(method: AntMethod) {
        this.method = method
    }

    fun setCache(cache: Boolean) {
        this.cache = cache
    }

    fun setContext(context: Context){
        this.context = context
    }


    fun addHeader(headers: HashMap<String, String>) {
        this.headers = headers
    }


    fun addBody(body: String) {
        this.body = body
    }


    fun addSSLSocketFactory(socketFactory: SSLSocketFactory) {
        this.socketFactory = socketFactory
    }


    fun addInterceptor(interceptor: AntInterceptor?) {
        this.interceptor = interceptor
    }

   fun addConvert(convert: Convert) {
        this.convert = convert
    }

    fun <T>run(clazz: Class<T> ,callBack: ResponseListener<T>):Runnable = Runnable {
        val service: HttpService = HttpServiceImpl()
        if (url.isEmpty()) throw  IllegalArgumentException("请求的接口不能为空")
        service.addUrl(url)
        service.setCache(cache)
        service.setContext(context!!)
        service.addMethod(method)
        service.addHeader(headers)
        service.addBody(body)
        if (socketFactory != null){
            service.addSSLSocketFactory(socketFactory!!)
        }
        service.addConvert(convert)
        when(clazz){
            String::class.java ->service.addHttpCallBack(StringListener(callBack as ResponseListener<String>))
            JSONObject::class.java ->service.addHttpCallBack(JSONListener(callBack as ResponseListener<JSONObject>))
            ByteArray::class.java -> service.addHttpCallBack(ByteArrayListener(callBack as ResponseListener<ByteArray>))

            else -> service.addHttpCallBack(StringListener(callBack as ResponseListener<String>))

        }
        if (interceptor != null){
            service.addInterceptor(interceptor!!)
        }
        service.execute()
    }





}