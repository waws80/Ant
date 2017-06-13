package pw.androidthanatos.ant.img

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.IdRes
import android.widget.ImageView
import edu.t_imageloader.utils.BitmapUtils
import pw.androidthanatos.ant.Ant
import pw.androidthanatos.ant.HttpTask
import pw.androidthanatos.ant.R
import pw.androidthanatos.ant.cache.DoubleCache
import pw.androidthanatos.ant.cache.MemeryCache
import pw.androidthanatos.ant.controller.ResponseListener
import pw.androidthanatos.ant.threadpool.ThreadPoolManager

/**
 * 图片请求体
 */
class LoadImage {


    private lateinit var url: String

    private lateinit var target: ImageView

    private var cache: AntCacheType = AntCacheType.MEMERYCACHE

    private var net: Boolean = true

    private var callBack: ImageCallBack? = null

    private  var errorImgId = R.drawable.ant_errorimg


    private val uiHandler: Handler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                val imageHolder= msg!!.obj as ImageHolder
                val path = imageHolder.url
                val bitmap = imageHolder.bitmap
                val view = imageHolder.target
                if (view!!.tag.toString() == path){
                    view.setImageBitmap(bitmap)
                    if (callBack != null){
                        callBack!!.complate()
                    }

                }
            }
        }
    }



    fun addUrl(url: String){
        this.url = url
    }

    fun setCache(cacheType: AntCacheType){
        this.cache =cacheType
    }

    fun setErrorImgId(@IdRes id: Int){
        this.errorImgId = id
    }

    fun addTarget(target: ImageView){
        this.target = target
    }


    /**
     * 获取图片
     */
    fun load(net: Boolean,callBack: ImageCallBack?){
        target.tag = url
        if (callBack != null){
            this.callBack = callBack
        }
        this.net = net
        val bitmap:Bitmap?
        when(cache){
            AntCacheType.MEMERYCACHE ->{
                bitmap = MemeryCache().getCache(url)
                check(bitmap,url,target,cache)
            }
            AntCacheType.DOUBLECACHE ->{
                bitmap = DoubleCache().getCache(url)
                check(bitmap,url,target,cache)
            }
            else -> throw IllegalArgumentException("缓存参数错误")
        }

    }

    /**
     * 校验bitmap
     */
    private fun check(bitmap: Bitmap?, url: String, target: ImageView,cacheType: AntCacheType){
        when (bitmap){
            null ->{
                if (net){
                    getNetImg( url, target ,cacheType)
                }else{
                    getNativeImg(url, target ,cacheType)
                }
            }
            else -> postBitmap(url,target,bitmap)
        }
    }

    /**
     * 获取网络图片
     */
    private fun getNetImg(url: String,target: ImageView,cacheType: AntCacheType){
        val task =HttpTask()
        if (!url.startsWith("http") || !url.startsWith("https")) throw IllegalArgumentException("请求图片的url错误")
        task.addUrl(url)
        task.setContext(target.context)
        val run = task.run(ByteArray::class.java,object: ResponseListener<ByteArray>(){

            override fun progress(progress: Int) {
                super.progress(progress)
                uiHandler.post {
                    if (url == target.tag){
                        if (callBack != null){
                            callBack!!.progress(progress)
                        }

                    }

                }
            }

            override fun complate(t: ByteArray) {
                    val bitmap = BitmapUtils.getNetBitmap(t,target)
                    if (bitmap != null){
                        when(cacheType){
                            AntCacheType.MEMERYCACHE ->{
                                val m = MemeryCache()
                                m.putCache(url,bitmap)
                                val b = m.getCache(url)
                                postBitmap(url,target,b!!)
                            }
                            AntCacheType.DOUBLECACHE ->{
                                val d = DoubleCache()
                                d.putCache(url,bitmap)
                                val b = d.getCache(url)
                                postBitmap(url,target,b!!)
                            }
                        }

                    }else{
                        Ant.antLog("请求图片错误。bitmap为空")
                        uiHandler.post {
                            if (url == target.tag){
                                if (callBack != null){
                                    callBack!!.error()
                                }
                                target.setBackgroundResource(errorImgId)
                            }

                        }
                    }
            }

            override fun failer(errorCode: Int) {
                Ant.antLog("请求图片错误。错误码： $errorCode")
                uiHandler.post {
                    if (url == target.tag){
                        if (callBack != null){
                            callBack!!.error()
                        }
                        target.setBackgroundResource(errorImgId)
                    }

                }
            }

        })
        ThreadPoolManager.creat().execute(run)
    }

    /**
     * 获取本地图片
     */
    private fun getNativeImg(url: String,target: ImageView,cacheType: AntCacheType){
        val bitmap = BitmapUtils.getLocalBitmap(url,target)
        if (bitmap != null){
            when(cacheType){
                AntCacheType.MEMERYCACHE ->{
                    val m = MemeryCache()
                    m.putCache(url,bitmap)
                    val b = m.getCache(url)
                    postBitmap(url,target,b!!)
                }
                AntCacheType.DOUBLECACHE ->{
                    val d = DoubleCache()
                    d.putCache(url,bitmap)
                    val b = d.getCache(url)
                    postBitmap(url,target,b!!)
                }
            }

        }else{
            uiHandler.post {
                if (url == target.tag){
                    Ant.antLog("请求图片错误。bitmap为空")
                    if (callBack != null){
                        callBack!!.error()
                    }
                    target.setBackgroundResource(errorImgId)
                }

            }
        }
    }



    private inner class ImageHolder{
        var url: String? = null
        var target: ImageView? = null
        var bitmap: Bitmap? = null
    }

    private fun postBitmap( path: String, imageView: ImageView, bitmap: Bitmap){
        val message = Message.obtain()
        val holder =  ImageHolder()
        holder.bitmap = bitmap
        holder.url = path
        holder.target = imageView
        message.obj = holder
        uiHandler.sendMessage(message)
    }




}