package pw.androidthanatos.ant.cache;

import android.graphics.Bitmap;

/**
 * 双缓存类
 */

class DoubleCache: ImageCache {
    private var memeryCache: ImageCache = MemeryCache()
    private var  disCache: ImageCache

    constructor (path: String = "Ant-http-imageCache", precent: Int = 70) {
        disCache= DiskCache(path,precent)
    }

    override fun getCache(path: String): Bitmap? {
        var bitmap = memeryCache.getCache(path)
        if (bitmap == null){
            bitmap=disCache.getCache(path)
        }
        return bitmap
    }

    override fun putCache(path: String, bitmap: Bitmap) {
        memeryCache.putCache(path, bitmap)
        disCache.putCache(path, bitmap)
    }
}
