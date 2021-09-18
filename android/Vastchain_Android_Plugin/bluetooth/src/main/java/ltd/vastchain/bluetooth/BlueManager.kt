package ltd.vastchain.bluetooth

import android.content.Context
import android.content.Intent
import ltd.vastchain.jsbridge.util.LogUtil

/**
 * Created by admin on 2021/9/18.
 */
object BlueManager {

	fun start(context: Context, url: String) {
		LogUtil.e("url:$url")
		val intent = Intent(context, BluetoothActivity::class.java)
		intent.putExtra("url", url)
		context.startActivity(intent)
	}
}