package ltd.vastchain.bluetooth.bridge

import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import ltd.vastchain.bluetooth.BlueManager
import ltd.vastchain.bluetooth.bridge.NativeBridge.Native.GET_APP_INFO
import ltd.vastchain.bluetooth.bridge.NativeBridge.Native.NAVIGATE_BACK
import ltd.vastchain.jsbridge.JsCallback
import ltd.vastchain.jsbridge.util.LogUtil

/**
 * Created by admin on 2022/2/22.
 */
class NativeBridge(var webView: WebView, val activity: Activity) {

    object Type {
        const val ROUTEGOBACK = "route/back";
    }

    object Native {
        const val NAVIGATE_BACK = "navigateBack"
        const val GET_APP_INFO = "getAppInfo"
    }

    val callback: JsCallback by lazy { JsCallback(this.webView, callbackId = "receiveMessage") }

    @JavascriptInterface
    fun invoke(method: String, params: String?) {
        LogUtil.e("cxd", "method:" + method)
        LogUtil.e("cxd", "params:" + params)
        when (method) {
            NAVIGATE_BACK -> activity.finish()
            GET_APP_INFO -> getAPPInfo()
        }
    }

    private fun getAPPInfo() {
        callback.invoke(GET_APP_INFO, BlueManager.getListener()?.getDataFromApp(GET_APP_INFO))
    }


    fun goBack() {
        callback.invoke(Type.ROUTEGOBACK)
    }


}