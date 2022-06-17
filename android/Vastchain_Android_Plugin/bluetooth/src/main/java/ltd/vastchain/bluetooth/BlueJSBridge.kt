package ltd.vastchain.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import ltd.vastchain.bluetooth.model.PrintModel
import ltd.vastchain.jsbridge.CoreJsCallback
import ltd.vastchain.jsbridge.util.JSONUtil
import ltd.vastchain.jsbridge.util.LogUtil
import ltd.vastchain.qrscan.QrScanManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by admin on 2021/9/10.
 */

class BlueJSBridge(var webView: WebView, val activity: Activity) {

	val TAG = "BlueJSBridge"

	companion object {
		const val SET_UP = "openBluetoothAdapter"
		const val SCAN = "startBluetoothDevicesDiscovery"
		const val STOP_SCAN = "stopBluetoothDevicesDiscovery"
		const val CONNECT = "createBLEConnection"
		const val DISCONNECT = "closeBLEConnection"
		const val WRITE = "writeBLECharacteristicValue"
		const val READ = "readBLECharacteristicValue"
		const val MTU = "setBLEMTU"
		const val GET_BONDED_DEVICES = "getBondedDevices"
		const val SCAN_QR_CODE = "scanQrCode"
		const val PRINT_DATA = "printData"
		const val LOG = "log"
		const val OPEN_WAREHOUSE_ACTIVITY = "openWareHouseActivity"
		const val CLOSE_WEB_VIEW = "closeWebView"
	}

	private var blueListener: IBlueListener? = null

	var checkPermission: ((callback: CoreJsCallback) -> Unit)? = null

	var bluetoothPlugin: IBluePlugin? = null
	val callback: CoreJsCallback by lazy { CoreJsCallback(this.webView) }

	@JavascriptInterface
	fun blueInvoke(method: String, params: String?) {
		try {
			LogUtil.e("cxd", "method:" + method)
			params?.let {
				LogUtil.e("cxd", "params:" + it)
			}
			if (method == LOG) {
				return
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
				var timeout = params?.optLong("timeout") ?: 10
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
				LogUtil.e("待发送数据长度：" + data.length)
				bluetoothPlugin?.writeCharacteristic(data, length)
			}
			READ -> {
				bluetoothPlugin?.readCharacteristic()
			}
			MTU -> {
				var mtu = params?.optInt("mtu")
				bluetoothPlugin?.setMtu(mtu = mtu)
			}
			SCAN_QR_CODE -> {
				QrScanManager.start(webView.context, callback = callback)
			}
			GET_BONDED_DEVICES -> {
				bluetoothPlugin?.getBondedDevices()
			}
			PRINT_DATA -> {
				Log.e("Printer", params?.toString().orEmpty())
				var address = params?.optString("deviceId")
				var msg = params?.optJSONObject("msg")
				var url = msg?.optString("url")
				var qrCodeId = msg?.optString("qrCodeId")
				var name = msg?.optString("name")
				var packageCount = msg?.optString("packageCount")
				var totalCount = msg?.optString("totalCount")
				var orgName = msg?.optString("orgName")
				var storehouseName = msg?.optString("storehouseName")
				var storehouseOrgName = msg?.optString("storehouseOrgName")
				var printMode = PrintModel(url, qrCodeId, name, packageCount, totalCount, orgName, storehouseName, storehouseOrgName)
				Log.e(TAG, printMode.toString())
				if (address.isNullOrEmpty().not()) {
					bluetoothPlugin?.print(deviceId = address!!, printMode)
				}
			}
			CLOSE_WEB_VIEW -> {
				activity.finish()
			}

			OPEN_WAREHOUSE_ACTIVITY -> {
				BlueManager.getListener()?.openWareHouseActivity(params!!)
			}
			LOG -> {
				Log.e("H5Log", params?.optString("msg").orEmpty())
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
				callback.invoke(SCAN, JSONUtil.success(2, message = "停止搜索到设备"))
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

			override fun getBondedDevicesResult(device: Set<BluetoothDevice>?) {
				var devicesJSON = JSONArray()
				device?.forEach{
					devicesJSON.put(JSONObject().apply {
						put("deviceId", it.address)
						put("name", it.name)
					})
				}
				callback.invoke(GET_BONDED_DEVICES, JSONUtil.success().put("data", devicesJSON))
			}

			override fun printSuccess() {
				callback.invoke(PRINT_DATA, JSONUtil.success(message = "打印完成"))
			}
		}

		bluetoothPlugin?.setBlueListener(this.blueListener)
	}

}