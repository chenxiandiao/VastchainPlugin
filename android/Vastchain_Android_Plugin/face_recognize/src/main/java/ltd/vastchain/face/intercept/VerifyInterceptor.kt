package ltd.vastchain.face.intercept

import android.util.Log
import ltd.vastchain.face.FaceManager
import ltd.vastchain.face.http.FaceApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by admin on 2021/9/30.
 */
class VerifyInterceptor(private val requestId: String) : Interceptor {
	private var verifySuccess = false
	override fun proceed(file: String, interceptChain: InterceptChain) {
		if (checked()) {
			interceptChain.proceed(file)
			return
		}
		Log.e("cxd", "开始人脸比对")
		FaceManager.pauseCheck()
		verifySuccess = verifyCompare(file, requestId) == true
		showNextTips(interceptChain)
		Thread.sleep(2000)
		FaceManager.resumeCheck()
		return
	}

	override fun checked(): Boolean {
		return verifySuccess
	}

	override fun proceedBegin() {

	}


	private fun verifyCompare(fullPath: String, requestId: String): Boolean? {
		if (FaceManager.skipAllCheck) {
			return true
		}
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

	private fun showNextTips(chain: InterceptChain) {
		chain.getNextInterceptor()?.proceedBegin();
	}
}