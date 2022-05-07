package ltd.vastchain.pay.wx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ltd.vastchain.pay.PayCallback

/**
 * Created by admin on 2022/5/7.
 */
class WXPayBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION = "com.dxy.gaia.wx_pay_result"
        const val PARAM_CODE = "PARAM_CODE"

        const val CODE_SUCCESS = 0
        const val CODE_FAILURE = -1
        const val CODE_CANCEL = -2

    }

   var mPayCallback: PayCallback? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (mPayCallback == null) {
            return
        }
        when (intent?.getIntExtra(PARAM_CODE, CODE_FAILURE)) {
            CODE_SUCCESS -> mPayCallback?.onPaySuccess()
            CODE_CANCEL -> mPayCallback?.onPayCancel()
            else -> mPayCallback?.onPayFailure()
        }
    }

}