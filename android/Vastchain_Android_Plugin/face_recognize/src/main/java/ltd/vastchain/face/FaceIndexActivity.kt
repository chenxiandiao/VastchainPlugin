package ltd.vastchain.face

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.face.databinding.ActivityFaceIndexBinding

/**
 * Created by admin on 2021/10/8.
 */
class FaceIndexActivity : AppCompatActivity() {

	private var idCard: String? = null
	private var name: String? = null

	companion object {
		const val PARAMS_ID_CARD = "PARAMS_ID_CARD"
		const val PARAMS_NAME = "PARAMS_NAME"
		fun start(context: Context, idCard: String, name: String) {
			val intent = Intent(context, FaceIndexActivity::class.java)
			intent.putExtra(PARAMS_ID_CARD, idCard)
			intent.putExtra(PARAMS_NAME, name)
			context.startActivity(intent)
		}
	}

	private lateinit var activityFaceIndexBinding: ActivityFaceIndexBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityFaceIndexBinding = ActivityFaceIndexBinding.inflate(layoutInflater)
		setContentView(activityFaceIndexBinding.root)
		idCard = intent.getStringExtra(PARAMS_ID_CARD)
		name = intent.getStringExtra(PARAMS_NAME)
		initData()
		initListener()
	}

	private fun initData() {
		activityFaceIndexBinding.toolbar.tvTitle.text = "人脸识别模式选择"
		activityFaceIndexBinding.toolbar.ivBack.setOnClickListener{
			finish()
		}
	}

	private fun initListener() {
		activityFaceIndexBinding.tvGoFaceEyeMouth.setOnClickListener {
			FaceActivity.start(this, eyeSkip = false, mouthSkip = false, idCard, name)
			finish()
		}

		activityFaceIndexBinding.tvGoFaceEye.setOnClickListener {
			FaceActivity.start(this, eyeSkip = false, mouthSkip = true, idCard, name)
			finish()
		}

		activityFaceIndexBinding.tvGoFaceMouth.setOnClickListener {
			FaceActivity.start(this, eyeSkip = true, mouthSkip = false, idCard, name)
			finish()
		}

		activityFaceIndexBinding.tvGoFace.setOnClickListener {
			FaceActivity.start(this, eyeSkip = true, mouthSkip = true, idCard, name)
			finish()
		}
	}


}