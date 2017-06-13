package pw.androidthanatos.ant

import android.content.Context
import android.util.Log
import android.widget.ImageView
import org.json.JSONObject
import pw.androidthanatos.ant.controller.AntInterceptor
import pw.androidthanatos.ant.controller.AntMethod
import pw.androidthanatos.ant.controller.ResponseListener
import pw.androidthanatos.ant.convert.Convert
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

    private var debug: Boolean = false

    @JvmStatic fun init(context: Context){
        this.context = context
    }

    @JvmStatic fun openDebug(){
        debug = true
    }

    @JvmStatic fun addStringRequest(httpTask: HttpTask,responseListener: ResponseListener<String>){
        if (context == null) throw RuntimeException("请初始化Ant")
        httpTask.setContext(context!!)
        val run =   httpTask.run(String::class.java,responseListener)
        ThreadPoolManager.creat().execute(run)
    }

    @JvmStatic fun addJSONRequest(httpTask: HttpTask,responseListener: ResponseListener<JSONObject>){
        if (context == null) throw RuntimeException("请初始化Ant")
        httpTask.setContext(context!!)
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

    var socketFactory: SSLSocketFactory = DefaultSSLSocketFactory().getSSLSocketFactory()

    var convert: Convert? = null


    var interceptor: AntInterceptor? = null

    var cache: Boolean = false

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



private fun filterWrap(task: HttpTask,wrap: DataWrapper){
    task.addUrl(wrap.url)
    if (wrap.cache){
        task.setCache(wrap.cache)
    }
    task.addMethod(wrap.method)
    task.addHeader(wrap.headers)
    task.addBody(wrap.body)
    if (wrap.socketFactory != null){
        task.addSSLSocketFactory(wrap.socketFactory!!)
    }
    if (wrap.convert != null){
        task.addConvert(wrap.convert!!)
    }
    if (wrap.interceptor != null){
        task.addInterceptor(wrap.interceptor!!)
    }
}




