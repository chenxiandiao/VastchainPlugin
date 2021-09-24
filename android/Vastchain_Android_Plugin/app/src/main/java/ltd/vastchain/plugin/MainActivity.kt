package ltd.vastchain.plugin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.bluetooth.BlueManager

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		findViewById<View>(R.id.tv_go_web).setOnClickListener{
//			val intent = Intent(this, BluetoothActivity::class.java)
//			startActivity(intent)
//			BlueManager.start(this, "http://10.144.2.172:10086/#/subPackage/warehouseManage/pages/wareHouseOperation/index?token=MmoXuOXOnvy8_r0Qstk4al1pHgdq-mmH&orgID=139723245184659456")
//			BlueManager.start(this, "https://nimiq.github.io/qr-scanner/demo/")
//			BlueManager.start(this, "https://cozmo.github.io/jsQR")
			BlueManager.start(this, "https:/www.baidu.com")
		}
	}
}