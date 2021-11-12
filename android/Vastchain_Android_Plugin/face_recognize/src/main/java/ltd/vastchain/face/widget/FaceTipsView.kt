package ltd.vastchain.face.widget

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ltd.vastchain.face.IFaceListener
import ltd.vastchain.face.R
import android.content.ContentResolver
import android.net.Uri


/**
 * Created by admin on 2021/9/29.
 */
class FaceTipsView @kotlin.jvm.JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),IFaceListener {


	private var tvTips: TextView? = null
	var mediaPlayer: MediaPlayer? = null

	init {
		View.inflate(context, R.layout.view_face_tips_2, this)
		tvTips = findViewById(R.id.tv_tips)

		mediaPlayer = MediaPlayer()
	}

	override fun beginCompare() {
//		val uri: Uri =
//			Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/raw/" + "face_verify.aac")
//		mediaPlayer?.apply {
//			setAudioStreamType(AudioManager.STREAM_MUSIC)
//			setDataSource(context, uri)
//			prepare()
//			start()
//		}
		mediaPlayer = MediaPlayer.create(context, R.raw.face)
		mediaPlayer?.start()
	}

	override fun checkFace() {
		tvTips?.text = "请正对屏幕"
	}

	override fun compareFail(msg: String) {
		if (msg.isEmpty()) {
			tvTips?.text = "人脸识别失败,请稍后重试"
		} else {
			tvTips?.text = msg
		}
	}

	override fun compareSuccess() {
//		tvTips?.text = "请闭眼后再睁开"
	}

	override fun beginEyeCheck() {
		mediaPlayer = MediaPlayer.create(context, R.raw.eye)
		mediaPlayer?.start()
		tvTips?.text = "请闭眼后再缓慢睁眼"
//		tvTips?.text = "请缓慢眨眼"
	}

	override fun eyeCheckFail() {
		tvTips?.text = "请再次，闭眼后再睁开"
//		tvTips?.text = "请再次缓慢眨眼"
	}

	override fun eyeCheckSuccess() {
		tvTips?.text = "眨眼检测成功"
	}

	override fun beginMouthCheck() {
		mediaPlayer = MediaPlayer.create(context, R.raw.mouth)
		mediaPlayer?.start()
		tvTips?.text = "请张开嘴巴再合上"
//		tvTips?.text = "请缓慢张嘴"
	}

	override fun mouthCheckFail() {
		tvTips?.text = "请再次，张开嘴巴再合上"
//		tvTips?.text = "请再次缓慢张嘴"
	}

	override fun mouthCheckSuccess() {
		tvTips?.text = "张嘴检测成功"
	}

	override fun compareEnd() {
		tvTips?.text = "人脸检测完成"
	}



}