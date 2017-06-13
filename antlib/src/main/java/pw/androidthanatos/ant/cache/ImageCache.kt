package pw.androidthanatos.ant.cache;

import android.graphics.Bitmap;

/**
 * 缓存接口
 */

 interface ImageCache {

    fun getCache(path: String): Bitmap?
    fun putCache(path: String, bitmap: Bitmap)
}
