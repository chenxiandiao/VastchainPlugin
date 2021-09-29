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

	private fun saveImage(image: ImageProxy): String {

		val fullName = "face_photo" + System.currentTimeMillis() + ".jpg"
		Log.e("cxd", "face_photo" + image.toString())
		Log.e("cxd", fullName)
		val yBuffer = image.planes[0].buffer
		val uBuffer = image.planes[1].buffer
		val vBuffer = image.planes[2].buffer
		val ySize = yBuffer.remaining()
		val uSize = uBuffer.remaining()
		val vSize = vBuffer.remaining()

		val nv21 = ByteArray(ySize + uSize + vSize)

		yBuffer.get(nv21, 0, ySize);
		vBuffer.get(nv21, ySize, vSize);
		uBuffer.get(nv21, ySize + vSize, uSize);

		val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)


//			输出流
		val out = ByteArrayOutputStream();
		//压缩写入out
		yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 20, out);
		//生成bitmap
		val bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().size)
		val rotate = rotateBitmap(bitmap, 270f)
		return saveBitmapToFile(rotate, fullName)
//			return "11"
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