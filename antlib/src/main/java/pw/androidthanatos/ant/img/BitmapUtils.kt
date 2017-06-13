package edu.t_imageloader.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import pw.androidthanatos.ant.img.ImageSizeUtil

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片处理类
 */

object BitmapUtils {


    private fun getBitmap(path: String , imageView: ImageView):Bitmap {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds=true
        BitmapFactory.decodeFile(path,options)
        val imageSize = ImageSizeUtil.getImageViewSize(imageView)
        options.inSampleSize=ImageSizeUtil.caculateInSampleSize(options,imageSize.width!!,imageSize.height!!)
        options.inJustDecodeBounds=false
        return BitmapFactory.decodeFile(path,options)
    }

    private fun  getBitmapFromByte( path: ByteArray, imageView: ImageView): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds=true
        BitmapFactory.decodeByteArray(path,0,path.size)
        val imageSize = ImageSizeUtil.getImageViewSize(imageView)
        options.inSampleSize=ImageSizeUtil.caculateInSampleSize(options,imageSize.width!!,imageSize.height!!)
        options.inJustDecodeBounds=false
        return  BitmapFactory.decodeByteArray(path,0,path.size)
    }


     fun getNetBitmap(byteArray: ByteArray,  imageView: ImageView): Bitmap? =  getBitmapFromByte(byteArray,imageView)


     fun getLocalBitmap( path: String,  imageView: ImageView): Bitmap? = getBitmap(path,imageView)


}
