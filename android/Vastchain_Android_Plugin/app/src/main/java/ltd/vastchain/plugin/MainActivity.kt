package ltd.vastchain.plugin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.bluetooth.BlueManager
import ltd.vastchain.face.FaceActivity
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

		activityMainBinding.tvGoFace.setOnClickListener {
			val intent = Intent(this, FaceActivity::class.java)
			startActivity(intent)
		}
	}
}