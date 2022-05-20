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
        val registered = wxApi?.registerApp(appId)
        if (registered == true) {
            Log.e("cxd", "微信注册成功")
        }
    }

    fun testPay() {
        val item = JSONObject()
        item.put("partnerId","1532938261")
        item.put("prepayId","1101000000140415649af9fc314aa427")
        item.put("packageValue","Sign=WXPay")
        item.put("nonceStr","1101000000140429eb40476f8896f4c9")
        item.put("timeStamp","1398746574")
        item.put("sign","7FFECB600D7157C5AA49810D2D8F28BC2811827B")
//        item.put("signType", "")
        pay(item)
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
        if (done == true) {
            Log.e("cxd", "发送支付信息")
        }
    }

    fun openWx() {
        wxApi?.openWXApp()
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