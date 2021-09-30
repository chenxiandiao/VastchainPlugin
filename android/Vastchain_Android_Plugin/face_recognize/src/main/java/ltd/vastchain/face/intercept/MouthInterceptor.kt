package ltd.vastchain.face.intercept

import android.util.Log
import ltd.vastchain.face.FaceManager

/**
 * Created by admin on 2021/9/30.
 */
class MouthInterceptor(private val requestId: String) : LiveInterceptor() {

	private var mouthCheck: Boolean = false
	private var mouthPhotos: MutableList<String> = mutableListOf()
	private var mouthTime = 0L

	companion object {
		private var PHOTO_MAX_SIZE = 20
	}

	override fun proceed(file: String, interceptChain: InterceptChain) {
		if (checked()) {
			interceptChain.proceed(file)
			return
		}
		FaceManager.listener?.beginMouthCheck()
		if (mouthPhotos.size == 0) {
			mouthTime = System.currentTimeMillis()
			Log.e("mouth", "图片第一张存储时间" + System.currentTimeMillis())
		}
		if (mouthPhotos.size == PHOTO_MAX_SIZE) {
			return
		}
		mouthPhotos.add(file)

		if (mouthPhotos.size == PHOTO_MAX_SIZE) {
			Log.e("mouth", "图片最后一张存储时间" + System.currentTimeMillis())
			Log.e("mouth", "存储图片耗时" + (System.currentTimeMillis() - mouthTime))
			FaceManager.pauseCheck()
			Log.e("mouth", "开始张嘴检测")
			val time = System.currentTimeMillis()
			val result = checkLive(mouthPhotos, "mouth", requestId)
			Log.e("mouth", "张嘴检测耗时：" + (System.currentTimeMillis() - time))
			if (result == true) {
				mouthCheck = true
				if (interceptChain.isLast()) {
					FaceManager.listener?.compareEnd()
				} else {
					showNextTips(chain = interceptChain)
					Thread.sleep(2000)
					FaceManager.resumeCheck()
				}
				Log.e("mouth", "张嘴检测通过")
			} else {
				FaceManager.listener?.mouthCheckFail()
				FaceManager.resumeCheck()
				Log.e("mouth", "张嘴检测未通过")
			}
		}
	}

	override fun checked(): Boolean {
		return mouthCheck
	}

	override fun proceedBegin() {
		FaceManager.listener?.beginMouthCheck()
	}
}