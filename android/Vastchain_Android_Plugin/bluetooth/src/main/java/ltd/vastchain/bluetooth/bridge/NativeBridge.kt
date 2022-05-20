package ltd.vastchain.bluetooth.bridge

import android.app.Activity
import android.content.Intent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import ltd.vastchain.bluetooth.BlueManager
import ltd.vastchain.bluetooth.BluetoothActivity
import ltd.vastchain.bluetooth.bridge.NativeBridge.Native.GET_APP_INFO
import ltd.vastchain.bluetooth.bridge.NativeBridge.Native.NAVIGATE_BACK
import ltd.vastchain.bluetooth.bridge.NativeBridge.Native.NAVIGATE_TO
import ltd.vastchain.bluetooth.bridge.NativeBridge.Native.TELE_CALL
import ltd.vastchain.jsbridge.JsCallback
import ltd.vastchain.jsbridge.util.LogUtil
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by admin on 2022/2/22.
 */
class NativeBridge(var webView: WebView, val activity: Activity) {

    object Type {
        const val ROUTEGOBACK = "route/back";
    }

    object Native {
        const val NAVIGATE_BACK = "navigateBack"
        const val NAVIGATE_TO = "navigateTo"
        const val GET_APP_INFO = "getAppInfo"
        const val TELE_CALL = "teleCall"
    }

    val callback: JsCallback by lazy { JsCallback(this.webView, callbackId = "receiveMessage") }

    var phoneCallback:((phone: String)->Unit)? = null

    @JavascriptInterface
    fun invoke(method: String, params: String?) {
        LogUtil.e("cxd", "method:" + method)
        LogUtil.e("cxd", "params:" + params)
        when (method) {
            NAVIGATE_BACK -> activity.finish()
            GET_APP_INFO -> getAPPInfo()
            TELE_CALL -> call(params)
            NAVIGATE_TO -> navigateTo(params)
        }
    }

    private fun getAPPInfo() {
        callback.invoke(GET_APP_INFO, BlueManager.getListener()?.getDataFromApp(GET_APP_INFO))
    }

    private fun call(params: String?) {
        try {
            val data = JSONObject(params)
            val phone = data.optString("phone")
            if(phone.isNullOrEmpty()) {
                return
            }
            phoneCallback?.invoke(phone)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun goBack() {
        callback.invoke(Type.ROUTEGOBACK)
    }

    private fun navigateTo(params: String?) {
        try {
            val data = JSONObject(params)
            val url = data.optString("url")
            val title = data.optString("title")
            if(url.isNullOrEmpty()) {
                return
            }
            val intent = Intent(activity, BluetoothActivity::class.java)
            intent.putExtra("url", BlueManager.urlPrefix + url)
            intent.putExtra("title", title)
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}