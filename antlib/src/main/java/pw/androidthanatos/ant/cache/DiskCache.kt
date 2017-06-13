package pw.androidthanatos.ant.cache;

import android.content.ContentValues.TAG
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import net.bither.util.NativeUtil;

import java.io.File;
import java.io.IOException;

import pw.androidthanatos.ant.img.MD5Utils;

/**
 * 硬盘缓存类
 */

 class DiskCache: ImageCache {

    private var  PERCENT=90

    private var cacheDir: String


    constructor (cacheDir: String, percent: Int) {
        this.cacheDir = cacheDir
        if (percent in 1..99){
            PERCENT=percent
        }

    }

    override fun getCache(path: String): Bitmap? {
        val file= File("sdcard/"+cacheDir, MD5Utils.getMd5(path)+".jpg")
        Log.w(TAG, "getCache: find image in $path diskcache")
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    override fun putCache(path: String, bitmap: Bitmap) {
        val f = File("sdcard/"+cacheDir)
        if (!f.exists()){
            f.mkdirs()
        }
        val file = File("sdcard/"+cacheDir,MD5Utils.getMd5(path)+".jpg")
        NativeUtil.compressBitmap(bitmap,PERCENT,file.absolutePath,true)
    }
}
