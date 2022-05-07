package ltd.vastchain.pay.bridge

import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView

import ltd.vastchain.jsbridge.JsCallback
import ltd.vastchain.jsbridge.util.LogUtil
import ltd.vastchain.pay.bridge.NativeBridge.Native.GET_APP_INFO
import ltd.vastchain.pay.bridge.NativeBridge.Native.NAVIGATE_BACK
import ltd.vastchain.pay.bridge.NativeBridge.Native.WX_PAY
import ltd.vastchain.pay.wx.WxPayHandler
import org.json.JSONObject

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
        const val WX_PAY = "wxPay"
    }

    val callback: JsCallback by lazy { JsCallback(this.webView, callbackId = "receiveMessage") }

    @JavascriptInterface
    fun invoke(method: String, params: String?) {
        LogUtil.e("cxd", "method:" + method)
        LogUtil.e("cxd", "params:" + params)
        when (method) {
            NAVIGATE_BACK -> activity.finish()
            GET_APP_INFO -> getAPPInfo()
            WX_PAY -> WxPayHandler.pay(JSONObject(params))
        }
    }

    private fun getAPPInfo() {

    }


    fun goBack() {
        callback.invoke(Type.ROUTEGOBACK)
    }


}