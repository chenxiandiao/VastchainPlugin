package ltd.vastchain.face

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.Log
import androidx.camera.core.ImageProxy
import ltd.vastchain.face.http.FaceApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


/**
 * Created by admin on 2021/9/28.
 */
@SuppressLint("StaticFieldLeak")
object FaceManager {

	var listener: IFaceListener? = null

	private var upload = true
	private var requestId: String = ""
	private var savePhoto: Boolean = false
	private var verifySuccess: Boolean = false
	private var eyeCheck: Boolean = false
	private var mouthCheck: Boolean = false

	private var PHOTO_MAX_SIZE = 20

	private var eyePhotos: MutableList<String> = mutableListOf()
	private var mouthPhotos: MutableList<String> = mutableListOf()

	@SuppressLint("StaticFieldLeak")
	private var context: Context? = null
	fun init(context: Context) {
		this.context = context
	}

	fun start(requestId: String) {
		savePhoto = true
		this.requestId = requestId
	}

	fun analyse(image: ImageProxy) {
		val fullPath = saveImage(image)
		addPhoto(fullPath)
	}

//	fun yuv420ToNv21(image: ImageProxy): ByteArray? {
//		val planes = image.planes
//		val yBuffer: ByteBuffer = planes[0].buffer
//		val uBuffer: ByteBuffer = planes[1].buffer
//		val vBuffer: ByteBuffer = planes[2].buffer
//		val ySize: Int = yBuffer.remaining()
//		val uSize: Int = uBuffer.remaining()
//		val vSize: Int = vBuffer.remaining()
//		val size = image.width * image.height
//		val nv21 = ByteArray(size * 3 / 2)
//		yBuffer.get(nv21, 0, ySize)
//		vBuffer.get(nv21, ySize, vSize)
//		val u = ByteArray(uSize)
//		uBuffer.get(u)
//
//		//每隔开一位替换V，达到VU交替
//		var pos = ySize + 1
//		for (i in 0 until uSize) {
//			if (i % 2 == 0) {
//				nv21[pos] = u[i]
//				pos += 2
//			}
//		}
//		return nv21
//	}
	@SuppressLint("UnsafeOptInUsageError")
	private fun saveImage(image: ImageProxy): String {
		val fullName = "face_photo" + System.currentTimeMillis() + ".jpg"
		Log.e("cxd", fullName)
		Log.e("cxd", image.format.toString())
		Log.e("cxd", image.width.toString())
		Log.e("cxd", image.height.toString())
		Log.e("cxd", image.planes[0].rowStride.toString())
		Log.e("cxd", image.planes[0].pixelStride.toString())
		Log.e("cxd", image.planes[1].rowStride.toString())
		Log.e("cxd", image.planes[1].pixelStride.toString())
		Log.e("cxd", image.planes[2].rowStride.toString())
		Log.e("cxd", image.planes[2].pixelStride.toString())
//		val yBuffer = image.planes[0].buffer
//		val uBuffer = image.planes[1].buffer
//		val vBuffer = image.planes[2].buffer
//		val ySize = yBuffer.remaining()
//		val uSize = uBuffer.remaining()
//		val vSize = vBuffer.remaining()
//		Log.e("cxd", "width height:" + image.width*image.height)
//		Log.e("cxd", "ySize:" + ySize + "uSize:" + uSize + "vSize：" + vSize)
//
//		val nv21 = ByteArray(ySize + uSize + vSize)
//
//		yBuffer.get(nv21, 0, ySize)
//		vBuffer.get(nv21, ySize, vSize)
//		uBuffer.get(nv21, ySize + vSize, uSize)
		val nv21 = yuv420ToNv21(image)

		val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)

		val out = ByteArrayOutputStream();
		//压缩写入out
		yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out);
		//生成bitmap
		val bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().size)
		val rotate = rotateBitmap(bitmap, 270f)
		return saveBitmapToFile(rotate, fullName)
//			return "11"
	}

	private fun yuv420ToNv21(image: ImageProxy):ByteArray? {
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
		var prefix = ""
		prefix = when {
			verifySuccess.not() -> {
				"compare/"
			}
			eyeCheck.not() -> {
				"eye/"
			}
			else -> {
				"mouth/"
			}
		}
		val fullPath =
			context?.getExternalFilesDir(null)?.absolutePath.orEmpty() + File.separator + prefix + fileName
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
		if (verifySuccess.not()) {
			Log.e("cxd", "开始人脸比对")
			pauseCheck()
			verifySuccess = verifyCompare(file) == true
			showCompareResult()
			Thread.sleep(2000)
			resumeCheck()
			return
		}
		if (eyeCheck.not()) {
			addPhotoForEyeCheck(file)
			return
		}
		if (mouthCheck.not()) {
			addPhotoForMouthCheck(file)
		}
	}

	private fun showCompareResult() {
		if (verifySuccess.not()) {
			listener?.compareFail()
		} else {
			listener?.compareSuccess()
		}
	}


	private fun verifyCompare(fullPath: String): Boolean? {
		val file = File(fullPath)
		val imgBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
		val part = MultipartBody.Part.createFormData("face_img", file.name, imgBody)
		try {
			val execute = FaceApi.faceApi.faceCompare(
				RequestBody.create(MediaType.parse("multipart/form-data"), requestId),
				part,
			).execute()
			return if (execute.isSuccessful) {
				Log.e("cxd", "图片上报成功")
				val response = execute.body()
				Log.e("cxd", response.toString())
				response?.success()
			} else {
				Log.e("cxd", "错误：" + execute.errorBody()?.string().orEmpty())
				false
			}
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {

		}
		return false
	}

	var eyeTime = 0L
	private fun addPhotoForEyeCheck(file: String) {
		if (eyePhotos.size == 0) {
			eyeTime = System.currentTimeMillis()
			Log.e("eye", "图片第一张存储时间" + System.currentTimeMillis())
		}
		if (eyePhotos.size == PHOTO_MAX_SIZE) {
			return
		}
		eyePhotos.add(file)
		if (eyePhotos.size == PHOTO_MAX_SIZE) {
			Log.e("eye", "图片最后一张存储时间" + System.currentTimeMillis())
			Log.e("eye", "存储图片耗时" + (System.currentTimeMillis() - eyeTime))
			pauseCheck()
			if (upload.not()) {
				return
			}
			Log.e("eye", "开始眨眼检测")
			val time = System.currentTimeMillis()
			val result = checkLive(eyePhotos, "eye")
			Log.e("eye", "眨眼检测耗时：" + (System.currentTimeMillis() -time))
			if (result == true) {
				eyeCheck = true
				listener?.eyeCheckSuccess()
				Thread.sleep(2000)
				resumeCheck()
				Log.e("eye", "眨眼检测通过")
			} else {
				listener?.eyeCheckFail()
				resumeCheck()
				Log.e("eye", "眨眼检测未通过")
			}
		}
	}

	var mouthTime = 0L
	private fun addPhotoForMouthCheck(file: String) {
		if (mouthPhotos.size == 0) {
			mouthTime = System.currentTimeMillis()
			Log.e("mouth", "图片第一张存储时间" + System.currentTimeMillis())
		}
		if (mouthPhotos.size == PHOTO_MAX_SIZE) {
			return
		}
		mouthPhotos.add(file)
//		Log.e("cxd", file)
		if (mouthPhotos.size == PHOTO_MAX_SIZE) {
			Log.e("mouth", "图片最后一张存储时间" + System.currentTimeMillis())
			Log.e("mouth", "存储图片耗时" + (System.currentTimeMillis() - mouthTime))
			pauseCheck()
			if (upload.not()) {
				return
			}
			Log.e("mouth", "开始张嘴检测")
			val time = System.currentTimeMillis()
			val result = checkLive(mouthPhotos, "mouth")
			Log.e("mouth", "张嘴检测耗时：" + (System.currentTimeMillis() -time))
			if (result == true) {
				mouthCheck = true
				listener?.mouthCheckSuccess()
				Log.e("mouth", "张嘴检测通过")
			} else {
				listener?.mouthCheckFail()
				resumeCheck()
				Log.e("mouth", "张嘴检测未通过")
			}
		}
	}


	fun needTakePhoto(): Boolean {
		return savePhoto
	}

	private fun resumeCheck() {
		savePhoto = true
		eyePhotos.clear()
		mouthPhotos.clear()
	}

	private fun pauseCheck() {
		Log.e("cxd", "pause")
		savePhoto = false
	}

	/**
	 * 上传文件到阿里
	 */

	private fun checkLive(photos: List<String>, type: String): Boolean? {
		Log.e(type, "请求发起：" + System.currentTimeMillis().toString())
		val parts = mutableListOf<MultipartBody.Part>()
		photos.forEach {
			val file = File(it)
			val imgBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
			val part = MultipartBody.Part.createFormData("face_imgs", file.name, imgBody)
			parts.add(part)
		}
		try {
			val execute = FaceApi.faceApi.uploadFile(
				RequestBody.create(MediaType.parse("multipart/form-data"), requestId),
				RequestBody.create(MediaType.parse("multipart/form-data"), type),
				parts,
			).execute()
			return if (execute.isSuccessful) {
				Log.e(type, "图片上报成功")
				val response = execute.body()
				Log.e(type, response.toString())
				response?.success()
			} else {
				Log.e(type, "图片上报失败")
				false
			}
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			Log.e(type, "请求结束" + System.currentTimeMillis().toString())

		}
		return false
	}

	fun clearDirectory() {
		val directory = File(context?.getExternalFilesDir(null)?.absolutePath.orEmpty())
		directory.listFiles().forEach {
			it.deleteRecursively()
//			it.delete()
		}
	}

	fun release() {
		savePhoto = false
		verifySuccess = false
		eyeCheck = false
		mouthCheck = false
		eyePhotos.clear()
		mouthPhotos.clear()
	}
}