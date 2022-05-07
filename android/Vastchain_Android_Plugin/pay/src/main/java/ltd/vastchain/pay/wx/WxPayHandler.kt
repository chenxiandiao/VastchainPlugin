package ltd.vastchain.pay.wx

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONObject

/**
 * Created by admin on 2022/4/29.
 */
@SuppressLint("StaticFieldLeak")
object WxPayHandler {
    const val TAG = "WxPayHandler"
    private var wxApi: IWXAPI? = null
    private var context: Context? = null
    private var appId: String? = null

    fun init(context: Context, appId: String?) {
        wxApi = WXAPIFactory.createWXAPI(context, null,false)

        // 将该app注册到微信
        this.appId = appId
        this.context = context.applicationContext
        wxApi?.registerApp(appId)
    }

    fun pay(item: JSONObject) {
        if(checkWXApi().not()) {
            return
        }
        if (context == null || appId == null) {
            Log.e(TAG, "请先初始化")
            return
        }
        val request = PayReq()
        request.appId = appId
        request.partnerId = item.optString("partnerId")
        request.prepayId = item.optString("prepayId")
        request.packageValue = item.optString("packageValue")
        request.nonceStr = item.optString("nonceStr")
        request.timeStamp = item.optString("timeStamp")
        request.sign = item.optString("sign")
        request.signType = item.optString("signType")
        request.extData = item.optString("extData")
        val done = wxApi?.sendReq(request)
    }

    private fun checkWXApi(): Boolean {
        if (wxApi?.isWXAppInstalled == false) {
            Toast.makeText(context, "您的系统未安装微信，请安装后再支付", Toast.LENGTH_LONG).show()
            return false
        }
        if (wxApi?.wxAppSupportAPI?:0 < com.tencent.mm.opensdk.constants.Build.PAY_SUPPORTED_SDK_INT) {
            Toast.makeText(context, "您的微信不支持微信支付，请检查微信版本是否过低", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun sendBroadcast(context: Context, code: Int) {
        val intent = Intent(WXPayBroadcastReceiver.ACTION)
        intent.putExtra(WXPayBroadcastReceiver.PARAM_CODE, code)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun release() {
        this.context = null
    }
}