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
			BlueManager.start(this, "http://www.baidu.com")
		}
	}
}