package ltd.vastchain.qrscan

import android.content.Context
import android.content.Intent
import ltd.vastchain.jsbridge.CoreJsCallback
import ltd.vastchain.jsbridge.util.JSONUtil
import ltd.vastchain.qrscan.QrScanActivity
import ltd.vastchain.qrscan.huawei.HwQrScanActivity

/**
 * Created by admin on 2021/9/27.
 */
object QrScanManager {

	const val SCAN_QR_CODE = "scanQrCode"

	var callback: CoreJsCallback? = null

	fun start(context: Context) {
//		val intent = Intent(context, HwQrScanActivity::class.java)
		val intent = Intent(context, QrScanActivity::class.java)
		context.startActivity(intent)
	}

	fun start(context: Context, callback: CoreJsCallback) {
		this.callback = callback
//		val intent = Intent(context, HwQrScanActivity::class.java)
		val intent = Intent(context, QrScanActivity::class.java)
		context.startActivity(intent)
	}

	fun invoke(qrcode: String) {
		var response = JSONUtil.success().put("data", qrcode)
		callback?.invoke(SCAN_QR_CODE, response)
	}
}