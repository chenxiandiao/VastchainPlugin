package ltd.vastchain.face.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ltd.vastchain.face.IFaceListener
import ltd.vastchain.face.R

/**
 * Created by admin on 2021/9/29.
 */
class FaceTipsView @kotlin.jvm.JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),IFaceListener {


	private var tvTips: TextView? = null

	init {
		View.inflate(context, R.layout.view_face_tips, this)
		tvTips = findViewById(R.id.tv_tips)
	}

	override fun compareFail() {
		tvTips?.text = "识别失败，请正对屏幕"
	}

	override fun compareSuccess() {
		tvTips?.text = "请闭眼后再睁开"
	}

	override fun eyeCheckFail() {
		tvTips?.text = "请再次，闭眼后再睁开"
	}

	override fun eyeCheckSuccess() {
		tvTips?.text = "请张开嘴巴再合上"
	}

	override fun mouthCheckFail() {
		tvTips?.text = "请再次，张开嘴巴再合上"
	}

	override fun mouthCheckSuccess() {
		tvTips?.text = "人脸检测成功"
	}


}