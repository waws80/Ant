package pw.androidthanatos.ant.img;

import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * imageview 大小工具类
 *
 */
object ImageSizeUtil {
	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	fun caculateInSampleSize( options: Options, reqWidth: Int, reqHeight: Int): Int {
		val width = options.outWidth
		val height = options.outHeight
		var inSampleSize = 1
		if (width > reqWidth || height > reqHeight) {
			val widthRadio = Math.round(width * 1.0f / reqWidth)
			val heightRadio = Math.round(height * 1.0f / reqHeight)
			inSampleSize = Math.max(widthRadio, heightRadio)
		}
		return inSampleSize
	}

	/**
	 * 根据ImageView获适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	fun getImageViewSize( imageView: ImageView): ImageSize {
		val imageSize = ImageSize()
		val displayMetrics:DisplayMetrics = imageView.context.resources.displayMetrics
		val lp = imageView.layoutParams
		var width = imageView.width// 获取imageview的实际宽度
		if (width <= 0) {
			if (lp!=null&&lp.width>=0)width = lp.width;// 获取imageview在layout中声明的宽度
		}
		if (width <= 0) {
			width = getImageViewFieldValue(imageView, "mMaxWidth")
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels
		}
		var height = imageView.height  // 获取imageview的实际高度
		if (height <= 0) {
			if (lp!=null&&lp.height>=0)height = lp.height// 获取imageview在layout中声明的宽度
		}
		if (height <= 0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight")// 检查最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels
		}
		imageSize.width = width
		imageSize.height = height

		return imageSize
	}

	class ImageSize {
		var width: Int? = null
		var height: Int? = null
	}
	
	/**
	 * 通过反射获取imageview的某个属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	fun getImageViewFieldValue( any: Any,  fieldName: String): Int {
		var value = 0
		try {
			val field = ImageView::class.java.getDeclaredField(fieldName)
			field.isAccessible = true
			val fieldValue = field.getInt(any)
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue
			}
		} catch ( e: Exception) {
			e.printStackTrace()
		}
		return value
	}
	
}
