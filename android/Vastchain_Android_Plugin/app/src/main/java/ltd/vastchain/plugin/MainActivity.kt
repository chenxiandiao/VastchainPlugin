package ltd.vastchain.plugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.bluetooth.BlueManager
import ltd.vastchain.face.PersonInfoActivity
import ltd.vastchain.plugin.databinding.ActivityMainBinding
import ltd.vastchain.qrscan.QrScanManager

class MainActivity : AppCompatActivity() {

	private lateinit var activityMainBinding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(activityMainBinding.root)

		activityMainBinding.tvGoWeb.setOnClickListener {
//			val intent = Intent(this, BluetoothActivity::class.java)
//			startActivity(intent)
//			BlueManager.start(this, "http://10.144.2.172:10086/#/subPackage/warehouseManage/pages/wareHouseOperation/index?token=MmoXuOXOnvy8_r0Qstk4al1pHgdq-mmH&orgID=139723245184659456")
//			BlueManager.start(this, "https://nimiq.github.io/qr-scanner/demo/")
//			BlueManager.start(this, "https://cozmo.github.io/jsQR")
			BlueManager.start(this, "http://10.144.1.116:8000")
		}

		activityMainBinding.tvGoScan.setOnClickListener {
//			val intent = Intent(this, TestScanActivity::class.java)
//			startActivity(intent)
			QrScanManager.start(this)
		}

		activityMainBinding.tvGoFaceHome.setOnClickListener {
			PersonInfoActivity.start(this)
		}

//		activityMainBinding.tvGoFaceEyeMouth.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = false, mouthSkip = false)
//		}
//
//		activityMainBinding.tvGoFaceEye.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = false, mouthSkip = true)
//		}
//
//		activityMainBinding.tvGoFaceMouth.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = true, mouthSkip = false)
//		}
//
//		activityMainBinding.tvGoFace.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = true, mouthSkip = true)
//		}

	}
}