package ltd.vastchain.face.intercept

import android.util.Log
import ltd.vastchain.face.FaceManager

/**
 * Created by admin on 2021/9/30.
 */
class EyeInterceptor(private val requestId: String) : LiveInterceptor() {

	private var eyeCheck: Boolean = false
	private var eyePhotos: MutableList<String> = mutableListOf()
	private var eyeTime = 0L
	private var tryCount = 0

	companion object {
		private var PHOTO_MAX_SIZE = 20
		private const val COMPARE_COUNT = 5
	}

	override fun proceed(file: String, interceptChain: InterceptChain) {
		if (checked()) {
			interceptChain.proceed(file)
			return
		}

//		FaceManager.listener?.beginEyeCheck()
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
			FaceManager.pauseCheck()
			Log.e("eye", "开始眨眼检测")
			val time = System.currentTimeMillis()
			val result = checkLive(eyePhotos, "eye", requestId)
			Log.e("eye", "眨眼检测耗时：" + (System.currentTimeMillis() - time))
			if (result == true) {
				eyeCheck = true
				if (interceptChain.isLast()) {
					FaceManager.listener?.compareEnd()
				} else {
					showNextTips(chain = interceptChain)
					Thread.sleep(1000)
					FaceManager.resumeCheck()
				}
				Log.e("eye", "眨眼检测通过")
			} else {
				tryCount++
				if (tryCount<= COMPARE_COUNT) {
					FaceManager.listener?.eyeCheckFail()
					eyePhotos.clear()
					FaceManager.resumeCheck()
					Log.e("eye", "眨眼检测未通过")
				} else {
					eyePhotos.clear()
					FaceManager?.listener?.compareFail("眨眼检测未通过")
				}
			}
		}
	}

	override fun checked(): Boolean {
		return eyeCheck
	}

	override fun proceedBegin() {
		FaceManager.listener?.beginEyeCheck()
	}


}