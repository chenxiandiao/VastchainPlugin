package ltd.vastchain.jsbridge

import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import ltd.vastchain.jsbridge.util.LogUtil

/**
 * Created by admin on 2022/2/22.
 */
class JsBridge(var webView: WebView, val activity: Activity) {

    object Type{
         const val ROUTEGOBACK  = "route/back";
    }

    val callback: JsCallback by lazy { JsCallback(this.webView, callbackId = "receiveMessage") }

    @JavascriptInterface
    fun invoke(method: String, params: String?) {
        LogUtil.e("cxd", "method:" + method)
        LogUtil.e("cxd", "params:" + params)
        when(method) {
            "navigateBack"-> activity.finish()
        }
    }


    fun goBack() {
        callback.invoke(Type.ROUTEGOBACK)
    }


}