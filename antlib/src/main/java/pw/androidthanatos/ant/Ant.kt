package pw.androidthanatos.ant

import android.content.Context
import android.util.Log
import android.widget.ImageView
import okhttp3.OkHttpClient
import org.json.JSONObject
import pw.androidthanatos.ant.controller.AntInterceptor
import pw.androidthanatos.ant.controller.AntMethod
import pw.androidthanatos.ant.controller.HttpTask
import pw.androidthanatos.ant.controller.ResponseListener
import pw.androidthanatos.ant.convert.Convert
import pw.androidthanatos.ant.convert.DefaultConvert
import pw.androidthanatos.ant.convert.OKHttpConvert
import pw.androidthanatos.ant.img.AntCacheType
import pw.androidthanatos.ant.img.ImageCallBack
import pw.androidthanatos.ant.img.LoadImage
import pw.androidthanatos.ant.sslsocket.DefaultSSLSocketFactory
import pw.androidthanatos.ant.threadpool.ThreadPoolManager
import javax.net.ssl.SSLSocketFactory

/**
 * 网络访问框架(图片   数据[json string] )
 * 支持dsl写法
 */
object Ant {

    val NETWORK_ERROR: Int = 101

    private var context: Context? = null

    lateinit var convert: Convert

    lateinit var sslSocketFactory: SSLSocketFactory

    var interceptor: AntInterceptor? = null

    private var debug: Boolean = false

    @JvmStatic fun init(context: Context,
                        convert: Convert = DefaultConvert(),
                        sslSocketFactory: SSLSocketFactory = DefaultSSLSocketFactory().getSSLSocketFactory(),
                        interceptor: AntInterceptor? = null){
       this.context = context
       this.convert = convert
       this.sslSocketFactory = sslSocketFactory
       this.interceptor = interceptor
        //判断当前项目是否已经依赖了okhttp
       if (convert is OKHttpConvert && Class.forName("okhttp3.OkHttpClient") == null){
           throw RuntimeException("请先添加 okhttp3.OkHttpClient")
       }


    }

    @JvmStatic fun openDebug(){
        debug = true
    }

    @JvmStatic fun addStringRequest(httpTask: HttpTask, responseListener: ResponseListener<String>){
        if (context == null) throw RuntimeException("请初始化Ant")
        httpTask.setContext(context!!)
        httpTask.addConvert(this.convert)
        httpTask.addSSLSocketFactory(this.sslSocketFactory)
        if (interceptor != null){
            httpTask.addInterceptor(interceptor!!)
        }

        val run =   httpTask.run(String::class.java,responseListener)
        ThreadPoolManager.creat().execute(run)
    }

    @JvmStatic fun addJSONRequest(httpTask: HttpTask, responseListener: ResponseListener<JSONObject>){
        if (context == null) throw RuntimeException("请初始化Ant")
        httpTask.setContext(context!!)
        httpTask.addConvert(this.convert)
        httpTask.addSSLSocketFactory(this.sslSocketFactory)
        if (interceptor != null){
            httpTask.addInterceptor(interceptor!!)
        }
        val run = httpTask.run(JSONObject::class.java,responseListener)
        ThreadPoolManager.creat().execute(run)
    }

    fun antLog(msg: String){
        if (debug){
            Log.d("Ant-----  ","内容："+msg)
        }

    }
}

class DataWrapper{
    var url: String = ""

    var method: AntMethod = AntMethod.GET

    var headers: HashMap<String,String> = HashMap()

    var body: String = ""

    var cache: Boolean = false

    var convert: Convert = Ant.convert

    var sslSocketFactory: SSLSocketFactory = DefaultSSLSocketFactory().getSSLSocketFactory()

    var inteceptor: AntInterceptor? = null

    var _progress:(Int) ->Unit = {}

    var _complateString:(String) ->Unit = {}

    var _complateJson:(JSONObject) ->Unit = {}

    var _error: (Int) ->Unit = {}

    fun progress(p:(Int) ->Unit){
        _progress = p
    }
    fun complateString(complate: (String) ->Unit){
        _complateString = complate
    }

    fun complateJson(complate: (JSONObject) ->Unit){
        _complateJson = complate
    }

    fun error(failer: (Int) ->Unit){
        _error = failer
    }
}



class ImgWrapper{
    var url: String = ""

    var target: ImageView? = null

    var cache: AntCacheType = AntCacheType.MEMERYCACHE

    var net: Boolean = true

    var errorImgId = R.drawable.ant_errorimg

    var _progress:(Int) ->Unit = {}

    var _complate:() ->Unit = {}

    var _error:() ->Unit = {}

    fun progress(progress:(Int) ->Unit){
        this._progress =progress
    }

    fun complate(complate:() ->Unit){
        this._complate = complate
    }

    fun error(error:() ->Unit){
        this._error =error
    }
}

fun antString(init: DataWrapper.() ->Unit){
    val wrap = DataWrapper()
    wrap.init()
    val task = HttpTask()
    filterWrap(task, wrap)
    Ant.addStringRequest(task,object : ResponseListener<String>(){

        override fun progress(progress: Int) {
            super.progress(progress)
            wrap._progress(progress)
        }

        override fun complate(t: String) {
            wrap._complateString(t)
        }

        override fun failer(errorCode: Int) {
            wrap._error(errorCode)
        }

    })
}



fun antJSON(init: DataWrapper.() ->Unit){
    val wrap = DataWrapper()
    wrap.init()
    val task = HttpTask()
    filterWrap(task, wrap)
    Ant.addJSONRequest(task,object : ResponseListener<JSONObject>(){

        override fun progress(progress: Int) {
            super.progress(progress)
            wrap._progress(progress)
        }

        override fun complate(t: JSONObject) {
            wrap._complateJson(t)
        }

        override fun failer(errorCode: Int) {
            wrap._error(errorCode)
        }

    })
}

fun antImg(inti: ImgWrapper.() ->Unit){
    val wrap = ImgWrapper()
    wrap.inti()
    val loadImg = LoadImage()
    loadImg.addUrl(wrap.url)
    loadImg.addTarget(wrap.target!!)
    loadImg.setCache(wrap.cache)
    loadImg.setErrorImgId(wrap.errorImgId)
    loadImg.load(wrap.net,object: ImageCallBack{
        override fun progress(progress: Int) {
            wrap._progress(progress)
        }

        override fun complate() {
            wrap._complate()
        }

        override fun error() {
           wrap._error()
        }

    })
}



private fun filterWrap(task: HttpTask, wrap: DataWrapper){
    task.addUrl(wrap.url)
    if (wrap.cache){
        task.setCache(wrap.cache)
    }
    task.addMethod(wrap.method)
    task.addHeader(wrap.headers)
    task.addBody(wrap.body)
    task.addConvert(wrap.convert)
    task.addSSLSocketFactory(wrap.sslSocketFactory)
    if (wrap.inteceptor != null){
        task.addInterceptor(wrap.inteceptor!!)
    }

}




