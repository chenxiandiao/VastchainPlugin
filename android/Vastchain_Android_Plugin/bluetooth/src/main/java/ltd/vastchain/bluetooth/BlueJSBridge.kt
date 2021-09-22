package ltd.vastchain.bluetooth

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import ltd.vastchain.jsbridge.CoreJsCallback
import ltd.vastchain.jsbridge.util.JSONUtil
import ltd.vastchain.jsbridge.util.LogUtil
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by admin on 2021/9/10.
 */

class BlueJSBridge(var webView: WebView) {

	companion object {
		const val SET_UP = "openBluetoothAdapter"
		const val SCAN = "startBluetoothDevicesDiscovery"
		const val STOP_SCAN = "stopBluetoothDevicesDiscovery"
		const val CONNECT = "createBLEConnection"
		const val DISCONNECT = "closeBLEConnection"
		const val WRITE = "writeBLECharacteristicValue"
		const val READ = "readBLECharacteristicValue"
		const val MTU = "setBLEMTU"
	}

	private var blueListener: IBlueListener? = null

	var checkPermission: ((callback: CoreJsCallback) -> Unit)? = null

	var bluetoothPlugin: BluetoothPlugin? = null
	val callback: CoreJsCallback by lazy { CoreJsCallback(this.webView) }

	@JavascriptInterface
	fun blueInvoke(method: String, params: String?) {
		try {
			Log.e("cxd", "method:" + method)
			params?.let {
				Log.e("cxd", "params:" + it)
			}
			var jsonObject: JSONObject? = null
			if (params != null) {
				jsonObject = JSONObject(params)
			}
//			val callback = CoreJsCallback(this.webView, method)
			invoke(method, jsonObject, callback)
		} catch (e: JSONException) {
			e.printStackTrace()
		}
	}

	@JavascriptInterface
	fun blueInvoke(method: String) {
		blueInvoke(method, null)
	}

	fun invoke(method: String, params: JSONObject?, callback: CoreJsCallback) {
		initBlueListener(callback)
		when (method) {
			SET_UP -> {
				bluetoothPlugin?.setUp()
				checkPermission?.invoke(callback)
			}
			SCAN -> {
				var timeout = params?.optLong("timeout") ?: 0
				bluetoothPlugin?.startScan21(timeout)
			}
			STOP_SCAN -> {
				bluetoothPlugin?.stopScan21()
			}
			CONNECT -> {
				var deviceId = params?.optString("deviceId")
				var timeout = params?.optLong("timeout") ?: 0
				bluetoothPlugin?.connect(deviceId, timeOut = timeout)
			}
			DISCONNECT -> {
				bluetoothPlugin?.disconnect()
			}
			WRITE -> {
				var data: String = params?.optString("data").orEmpty()
				val length: Int = params?.optInt("length") ?: 0
				LogUtil.e("待发送数据长度："+ data.length)
				bluetoothPlugin?.writeCharacteristic(data, length)
			}
			READ -> {
				bluetoothPlugin?.readCharacteristic()
			}
			MTU -> {
				var mtu = params?.optInt("mtu")
				bluetoothPlugin?.setMtu(mtu = mtu)
			}
			else -> {

			}
		}
	}

	private fun initBlueListener(callback: CoreJsCallback) {
		this.blueListener = object : IBlueListener {
			override fun setUpFail() {
				callback.invoke(SET_UP, JSONUtil.error(message = "请先初始化蓝牙"))
			}

			override fun unConnect() {
				callback.invoke(SET_UP, JSONUtil.error(message = "蓝牙设备未连接"))
			}

			override fun scanResult(address: String, name: String?) {
				var obj = JSONObject()
				obj.put("deviceId", address)
				if (name.isNullOrEmpty()) {
					obj.put("name", "")
				} else {
					obj.put("name", name)
				}
				callback.invoke(SCAN, JSONUtil.success().put("data", obj))
			}

			override fun scanStop() {
				callback.invoke(SCAN, JSONUtil.success(message = "停止搜索到设备"))
			}

			override fun scanStopByTimeOut() {
				callback.invoke(SCAN, JSONUtil.success(1, "超时停止扫描"))
			}

			override fun connectSuccess() {
				callback.invoke(CONNECT, JSONUtil.success(message = "连接成功"))
			}

			override fun connectFail(errorCode: Int, message: String) {
				callback.invoke(CONNECT, JSONUtil.error(errorCode, message))
			}

			override fun disconnectSuccess() {
				callback.invoke(DISCONNECT, JSONUtil.success(message = "成功断开蓝牙连接"))
			}

			override fun writeSuccess() {
				callback.invoke(WRITE, JSONUtil.success(message = "写入数据成功"))
			}

			override fun writeFail(message: String) {
				callback.invoke(WRITE, JSONUtil.error(message = message))
			}

			override fun readCallBack(data: String) {
				callback.invoke(READ, JSONUtil.success().put("data", data))
			}

			override fun setMtuSuccess() {
				callback.invoke(MTU, JSONUtil.success(message = "设置mtu成功"))
			}

			override fun setMtuFail() {
				callback.invoke(MTU, JSONUtil.error(message = "设置mtu失败"))
			}
		}

		bluetoothPlugin?.blueListener = this.blueListener
	}

}