package ltd.vastchain.face

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ltd.vastchain.face.databinding.ActivityFaceBinding
import ltd.vastchain.face.http.FaceApi
import ltd.vastchain.face.widget.FaceTipsView
import java.io.File


const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

/**
 * Created by admin on 2021/9/27.
 */
class FaceActivity : AppCompatActivity() {

	private lateinit var activityMainBinding: ActivityFaceBinding
	private var tipsView: FaceTipsView? = null
	private var eyeSkip: Boolean = false
	private var mouthSkip: Boolean = false
	private var idCard: String? = null
	private var name: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityMainBinding = ActivityFaceBinding.inflate(layoutInflater)
		setContentView(activityMainBinding.root)

		Log.e("cxd", getExternalFilesDir(null)?.absolutePath.orEmpty())
		Log.e("cxd", filesDir.absolutePath.orEmpty())

		initView()
		initData()
	}

	private fun initData() {
		eyeSkip = intent.getBooleanExtra(PARAMS_EYE, false) ?: false
		mouthSkip = intent.getBooleanExtra(PARAMS_MOUTH, false) ?: false
		idCard = intent.getStringExtra(PARAMS_ID_CARD)
		name = intent.getStringExtra(PARAMS_NAME)
		if (idCard.isNullOrEmpty() || name.isNullOrEmpty()) {
			return
		}
		FaceManager.init(this)
		GlobalScope.launch {
			try {
//				Log.e("cxd", Thread.currentThread().name)
				val requestId = FaceApi.faceApi.getRequestId(
					"AFA72CFB6FED0343F81FC94BB3D3FFC3",
					idCard!!,
					name!!
				).request_id
				FaceManager.config(eyeSkip, mouthSkip, requestId)
				FaceManager.clearDirectory()
				FaceManager.start()
			} catch (e: Exception) {
				e.printStackTrace()
				Log.e("cxd", Thread.currentThread().name)
				withContext(Dispatchers.Main) {
					Toast.makeText(this@FaceActivity, "获取会话信息失败,请检查网络是否正常", Toast.LENGTH_SHORT)
						.show()
				}
			}
		}
		FaceManager.listener = object : IFaceListener {
			override fun beginCompare() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.beginCompare()
				}
			}

			override fun compareFail() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.compareFail()
				}

			}

			override fun compareSuccess() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.compareSuccess()
				}
			}

			override fun beginEyeCheck() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.beginEyeCheck()
				}
			}

			override fun eyeCheckFail() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.eyeCheckFail()
				}

			}

			override fun eyeCheckSuccess() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.eyeCheckSuccess()
				}
			}

			override fun beginMouthCheck() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.beginMouthCheck()
				}
			}

			override fun mouthCheckFail() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.eyeCheckSuccess()
				}
			}

			override fun mouthCheckSuccess() {
				GlobalScope.launch(Dispatchers.Main) {
					tipsView?.mouthCheckSuccess()
				}
			}

			override fun compareEnd() {
				GlobalScope.launch(Dispatchers.Main) {
					Toast.makeText(this@FaceActivity, "人脸检测完成", Toast.LENGTH_LONG).show()
					tipsView?.compareEnd()
				}
			}
		}
		FaceManager.listener?.beginCompare()
	}

	private fun initView() {
		supportFragmentManager.beginTransaction().add(R.id.fragment, CameraFragment()).commit()
		tipsView = activityMainBinding.vFaceTips
	}

	override fun onResume() {
		super.onResume()
		// Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
		// be trying to set app to immersive mode before it's ready and the flags do not stick
	}

	/** When key down event is triggered, relay it via local broadcast so fragments can handle it */
	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		return when (keyCode) {
			KeyEvent.KEYCODE_VOLUME_DOWN -> {
				val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
				true
			}
			else -> super.onKeyDown(keyCode, event)
		}
	}

	override fun onBackPressed() {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
			// Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
			// (https://issuetracker.google.com/issues/139738913)
			finishAfterTransition()
		} else {
			super.onBackPressed()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		FaceManager.release()
	}

	companion object {

		const val PARAMS_EYE = "eyeSkip"
		const val PARAMS_MOUTH = "mouthSkip"
		const val PARAMS_ID_CARD = "PARAMS_ID_CARD"
		const val PARAMS_NAME = "PARAMS_NAME"

		fun start(
			context: Context,
			eyeSkip: Boolean = false,
			mouthSkip: Boolean = false,
			idCard: String?,
			name: String?
		) {
			val intent = Intent(context, FaceActivity::class.java)
			intent.putExtra(PARAMS_EYE, eyeSkip)
			intent.putExtra(PARAMS_MOUTH, mouthSkip)
			intent.putExtra(PARAMS_ID_CARD, idCard)
			intent.putExtra(PARAMS_NAME, name)
			context.startActivity(intent)
		}

		/** Use external media if it is available, our app's file directory otherwise */
		fun getOutputDirectory(context: Context): File {
			val appContext = context.applicationContext
			val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
				File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
			}
			return if (mediaDir != null && mediaDir.exists())
				mediaDir else appContext.filesDir
		}
	}
//	private fun hideSystemUI() {
//		WindowCompat.setDecorFitsSystemWindows(window, false)
//		WindowInsetsControllerCompat(window, activityMainBinding.fragmentContainer).let { controller ->
//			controller.hide(WindowInsetsCompat.Type.systemBars())
//			controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//		}
//	}
}