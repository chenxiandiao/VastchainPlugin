package ltd.vastchain.qrscan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
import ltd.vastchain.jsbridge.util.LogUtil
import ltd.vastchain.qrscan.databinding.ActivityQrScanBinding

/**
 * Created by admin on 2021/9/27.
 */
class QrScanActivity: AppCompatActivity(),  QRCodeView.Delegate {

	companion object {
		private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
	}

	private lateinit var binding: ActivityQrScanBinding
	private var permission = arrayOf(Manifest.permission.CAMERA)
	private lateinit var mZXingView: ZXingView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityQrScanBinding.inflate(layoutInflater)
		setContentView(binding.root)
		mZXingView = binding.zxingview
		mZXingView.setDelegate(this)
		checkCamera()
		initListener()
	}

	private fun initListener() {
		binding.llFlashlight.setOnClickListener {
			it.isSelected = !it.isSelected
			if (!it.isSelected) {
				binding.zxingview.closeFlashlight()
				binding.tvFlashlight.text = "轻触照亮"
			} else {
				binding.zxingview.openFlashlight()
				binding.tvFlashlight.text = "轻触关闭"
			}
		}

		binding.ivBack.setOnClickListener {
			finish()
		}
	}


	private fun checkCamera() {
		if (checkPermission(this, permission).not()) {
			ActivityCompat.requestPermissions(
				this,
				permission,
				REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
			)
		} else {
			starPreview()
		}
	}

	private fun checkPermission(context: Context?, permissions: Array<String>): Boolean {
		if (context == null) {
			return false
		}
		for (permission in permissions) {
			if (ActivityCompat.checkSelfPermission(
					context,
					permission
				) != PackageManager.PERMISSION_GRANTED
			) {
				return false
			}
		}
		return true
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
				if (checkPermission(context = this, permission)) {
					starPreview()
				} else {
					Toast.makeText(this, "请授权应用权限", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	private fun starPreview() {
		mZXingView.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
		mZXingView.startSpotAndShowRect() // 显示扫描框，并开始识别
	}

	override fun onScanQRCodeSuccess(result: String?) {
		LogUtil.e("扫描到的结果:$result")
		result?.let {
			QrScanManager.invoke(it)
		}
//		mZXingView.startSpot() // 开始识别
	}

	override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
//		LogUtil.e("isDark:$isDark")
	}


	override fun onScanQRCodeOpenCameraError() {
		LogUtil.e("打开相机出错")
	}

	override fun onStop() {
		super.onStop()
		mZXingView.stopCamera()
	}

	override fun onDestroy() {
		super.onDestroy()
		mZXingView.onDestroy()
	}
}