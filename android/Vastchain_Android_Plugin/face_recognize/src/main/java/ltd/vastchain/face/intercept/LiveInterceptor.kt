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
abstract class LiveInterceptor : Interceptor {
	fun checkLive(photos: List<String>, type: String, requestId: String): Boolean? {
		if (FaceManager.skipAllCheck) {
			return true
		}
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

	fun showNextTips(chain: InterceptChain) {
		chain.getNextInterceptor()?.proceedBegin();
	}
}