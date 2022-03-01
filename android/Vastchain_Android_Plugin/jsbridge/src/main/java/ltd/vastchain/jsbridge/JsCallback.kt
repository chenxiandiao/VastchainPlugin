package ltd.vastchain.jsbridge

import android.webkit.WebView
import ltd.vastchain.jsbridge.util.LogUtil
import org.json.JSONObject

/**
 * Created by admin on 2021/9/13.
 */
class JsCallback(private val webView: WebView?, private val method: String? = null, private val callbackId: String) {
	fun invoke(method: String? = null, response: JSONObject? = null) {
		LogUtil.e(method + "Callback")
		var localMethod = method ?: this.method
		var res = response
		if (response != null) {
			LogUtil.e("返回给js的内容：$response")
		} else {
			res = JSONObject()
		}
		if(res?.has("code") == false) {
			res?.put("code", 0)
		}
		val callback: String = "window.jsBridge.$callbackId('$localMethod', $res)"
		LogUtil.e(callback)
		webView?.post{
			webView?.evaluateJavascript(callback) {
				LogUtil.e("cxd", "js回调：$it")
			}
		}
	}
}