package pw.androidthanatos.ant.cache

import android.graphics.Bitmap
import android.util.LruCache
import pw.androidthanatos.ant.Ant

/**
 * 内存缓存
 */

 class MemeryCache: ImageCache {



    private val cacheSize= (Runtime.getRuntime().maxMemory()/8).toInt()

    private val mCache: LruCache<String, Bitmap> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        object: LruCache<String, Bitmap>(cacheSize){
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return value!!.rowBytes*value.height
            }
        }
    }


    override fun getCache(path: String): Bitmap? {
        Ant.antLog("getCache: find image in $path memerycache")
        return  mCache.get(path)
    }

    override fun putCache(path: String, bitmap: Bitmap) {
        mCache.put(path,bitmap)
    }
}
