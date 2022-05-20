package ltd.vastchain.bluetooth

import android.content.Context
import android.content.Intent
import android.webkit.WebChromeClient
import ltd.vastchain.jsbridge.util.LogUtil

/**
 * Created by admin on 2021/9/18.
 */
object BlueManager {

	private var iWebViewListener: IWebViewListener? = null
	private var webChromeClient: WebChromeClient? = null

	var urlPrefix = ""


	fun getListener(): IWebViewListener? {
		return iWebViewListener
	}

	fun setListener(listener: IWebViewListener) {
		this.iWebViewListener = listener
	}

	fun setChromeClient(client: WebChromeClient) {
		this.webChromeClient = client
	}

	fun getChromeClient(): WebChromeClient? {
		return webChromeClient
	}

	fun start(context: Context, url: String, title: String?="", initBlue: Boolean = false) {
		LogUtil.e("url:$url")
		val intent = Intent(context, BluetoothActivity::class.java)
		intent.putExtra("url", url)
		intent.putExtra("title", title)
		intent.putExtra("initBlue", initBlue)
		context.startActivity(intent)
	}
}