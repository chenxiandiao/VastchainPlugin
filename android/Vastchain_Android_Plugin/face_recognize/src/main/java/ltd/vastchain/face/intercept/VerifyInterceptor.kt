package ltd.vastchain.face.intercept

import android.util.Log
import ltd.vastchain.face.FaceManager
import ltd.vastchain.face.http.FaceApi
import ltd.vastchain.face.model.BasicResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by admin on 2021/9/30.
 */
class VerifyInterceptor(private val requestId: String) : Interceptor {
	private var verifySuccess = false
	private var count = 0
	private val compareCount = 10
	override fun proceed(file: String, interceptChain: InterceptChain) {
		if (checked()) {
			interceptChain.proceed(file)
			return
		}
		Log.e("cxd", "开始人脸比对")
		FaceManager.pauseCheck()
		val response = verifyCompare(file, requestId)
		verifySuccess = response?.success() == true
		if (verifySuccess) {
			if (interceptChain.isLast()) {
				Log.e("VerifyInterceptor", "无活体检测")
				FaceManager.listener?.compareEnd()
			} else {
				showNextTips(interceptChain)
				Thread.sleep(1000)
				FaceManager.resumeCheck()
			}
		} else {
			count++
			if (count > compareCount) {
				FaceManager.listener?.compareFail(response?.msg.orEmpty())
			} else {
				FaceManager.resumeCheck()
			}
		}
		return
	}

	override fun checked(): Boolean {
		return verifySuccess
	}

	override fun proceedBegin() {

	}


	private fun verifyCompare(fullPath: String, requestId: String): BasicResponse? {
		if (FaceManager.skipAllCheck) {
			return BasicResponse("Ok", "Pass");
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
//				Log.e("cxd", "图片上报成功")
				val response = execute.body()
				response
//				Log.e("cxd", response.toString())
//				response?.success()
			} else {
//				Log.e("cxd", "错误：" + execute.errorBody()?.string().orEmpty())
//				false
				return BasicResponse("Fail", execute.errorBody()?.string().orEmpty())
			}
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {

		}
		return BasicResponse("Fail", "Pass");
	}

	private fun showNextTips(chain: InterceptChain) {
		chain.getNextInterceptor()?.proceedBegin();
	}
}