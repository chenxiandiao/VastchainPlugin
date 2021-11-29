package ltd.vastchain.face

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.Log
import androidx.camera.core.ImageProxy
import ltd.vastchain.face.intercept.*
import java.io.*
import android.media.FaceDetector
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import ltd.vastchain.face.utils.SPUtils


/**
 * Created by admin on 2021/9/28.
 */
@SuppressLint("StaticFieldLeak")
object FaceManager {

	var listener: IFaceListener? = null

	private var faceCallBack: IFaceCallBack? = null;

	var skipAllCheck = false
	private var savePhoto: Boolean = false

	private var interceptors: MutableList<Interceptor> = mutableListOf()
	private var chain: InterceptChain? = null

	@SuppressLint("StaticFieldLeak")
	private var context: Context? = null
	fun init(context: Context) {
		this.context = context
	}

	fun config(eyeSkip: Boolean, mouthSkip: Boolean, requestId: String) {
		interceptors.add(VerifyInterceptor(requestId))
		if (eyeSkip.not()) {
			interceptors.add(EyeInterceptor(requestId))
		}
		if (mouthSkip.not()) {
			interceptors.add(MouthInterceptor(requestId))
		}
		chain = InterceptChain(interceptors, 0)
	}

	fun start() {
		this.savePhoto = true
	}

	fun analyse(image: ImageProxy) {
		val fullPath = saveImage(image)
		addPhoto(fullPath)
	}


	@SuppressLint("UnsafeOptInUsageError")
	private fun saveImage(image: ImageProxy): String {
		val fullName = "face_photo" + System.currentTimeMillis() + ".jpg"
		Log.e("cxd", fullName)

		val nv21 = yuv420ToNv21(image)
		val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
		val out = ByteArrayOutputStream();
		//压缩写入out
		yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out);
		//生成bitmap
		val bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().size)
		val rotate = rotateBitmap(bitmap, 270f)

		val filePath = saveBitmapToFile(rotate, fullName)
		return if(detectFace(filePath)){
			filePath
		} else {
			File(filePath).delete()
			""
		}
	}

	private fun detectFace(file: String):Boolean {
		val time = System.currentTimeMillis()
		val option = BitmapFactory.Options()
		option.inPreferredConfig = Bitmap.Config.RGB_565
		val bitmap = BitmapFactory.decodeFile(file, option)
		if (bitmap != null) {
			val mImageWidth = bitmap.width
			val mImageHeight = bitmap.height
			val mFaces = arrayOfNulls<FaceDetector.Face>(1)
			val mFaceDetector = FaceDetector(mImageWidth, mImageHeight, 1)
			val mNumberOfFaceDetected = mFaceDetector.findFaces(bitmap, mFaces)
			Log.e("cxd", mNumberOfFaceDetected.toString())
			Log.e("cxd", "耗时：" + (System.currentTimeMillis() - time))
			return mNumberOfFaceDetected >= 1
		}
		return false

	}

	private fun yuv420ToNv21(image: ImageProxy): ByteArray? {
		try {
			val w: Int = image.width
			val h: Int = image.height
			// size是宽乘高的1.5倍 可以通过ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)得到
			val i420Size = w * h * 3 / 2
			val planes = image.planes
			//remaining0 = rowStride*(h-1)+w => 27632= 192*143+176 Y分量byte数组的size
			val remaining0: Int = planes[0].buffer.remaining()
			val remaining1: Int = planes[1].buffer.remaining()
			//remaining2 = rowStride*(h/2-1)+w-1 =>  13807=  192*71+176-1 V分量byte数组的size
			val remaining2: Int = planes[2].buffer.remaining()
			//获取pixelStride，可能跟width相等，可能不相等
			val pixelStride: Int = planes[2].pixelStride
			val rowOffest: Int = planes[2].rowStride
			val nv21 = ByteArray(i420Size)
			//分别准备三个数组接收YUV分量。
			val yRawSrcBytes = ByteArray(remaining0)
			val uRawSrcBytes = ByteArray(remaining1)
			val vRawSrcBytes = ByteArray(remaining2)
			planes[0].buffer.get(yRawSrcBytes)
			planes[1].buffer.get(uRawSrcBytes)
			planes[2].buffer.get(vRawSrcBytes)
			if (pixelStride == image.width) {
				//两者相等，说明每个YUV块紧密相连，可以直接拷贝
				System.arraycopy(yRawSrcBytes, 0, nv21, 0, rowOffest * h)
				System.arraycopy(vRawSrcBytes, 0, nv21, rowOffest * h, rowOffest * h / 2 - 1)
			} else {
				//根据每个分量的size先生成byte数组
				val ySrcBytes = ByteArray(w * h)
				val uSrcBytes = ByteArray(w * h / 2 - 1)
				val vSrcBytes = ByteArray(w * h / 2 - 1)
				for (row in 0 until h) {
					//源数组每隔 rowOffest 个bytes 拷贝 w 个bytes到目标数组
					System.arraycopy(yRawSrcBytes, rowOffest * row, ySrcBytes, w * row, w)
					//y执行两次，uv执行一次
					if (row % 2 == 0) {
						//最后一行需要减一
						if (row == h - 2) {
							System.arraycopy(
								vRawSrcBytes,
								rowOffest * row / 2,
								vSrcBytes,
								w * row / 2,
								w - 1
							)
						} else {
							System.arraycopy(
								vRawSrcBytes,
								rowOffest * row / 2,
								vSrcBytes,
								w * row / 2,
								w
							)
						}
					}
				}
				//yuv拷贝到一个数组里面
				System.arraycopy(ySrcBytes, 0, nv21, 0, w * h)
				System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1)
			}
			return nv21
		} catch (e: java.lang.Exception) {
			e.printStackTrace()
		}
		return null
	}

	private fun rotateBitmap(origin: Bitmap, degree: Float): Bitmap {
		val matrix = Matrix()
		matrix.preScale(1f, -1f)
		matrix.postRotate(degree)
		val bitmap =
			Bitmap.createBitmap(origin, 0, 0, origin.width, origin.height, matrix, false)
		origin.recycle()
		return bitmap
	}

	@Throws(IOException::class)
	private fun saveBitmapToFile(bitmap: Bitmap?, fileName: String): String {
		if (bitmap == null || TextUtils.isEmpty(fileName)) {
			return ""
		}
		var prefix = chain?.currentType()
		val fullPath =
			context?.getExternalFilesDir(null)?.absolutePath.orEmpty() + File.separator + prefix + fileName
		Log.e("cxd", fullPath)
		val file = File(fullPath)
		if (!file.exists() && file.parentFile.mkdirs() && !file.createNewFile()) {
			return ""
		}
		val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
		bitmap.compress(Bitmap.CompressFormat.JPEG, 20, os)
		os.close()
		return fullPath
	}

	private fun addPhoto(file: String) {
		if (file.isEmpty()) {
			Log.e("cxd", "无人脸框")
			Log.e("cxd", chain?.currentType().orEmpty())
			if (chain?.currentType() == "compare/") {
				//人脸识别过程中没有人脸，提示请正对人脸框
				listener?.checkFace()
			}
			return
		}
		chain?.proceed(file)
	}

	fun needTakePhoto(): Boolean {
		return savePhoto
	}

	fun resumeCheck() {
		savePhoto = true
	}

	fun pauseCheck() {
		savePhoto = false
	}


	fun clearDirectory() {
		val directory = File(context?.getExternalFilesDir(null)?.absolutePath.orEmpty())
		directory.listFiles().forEach {
			it.deleteRecursively()
		}
	}

	fun release() {
		savePhoto = false
		interceptors.clear()
	}

	fun getFaceCallBack(): IFaceCallBack? {
		return faceCallBack
	}

	fun setFaceCallBack(callback: IFaceCallBack) {
		this.faceCallBack = callback
	}
}