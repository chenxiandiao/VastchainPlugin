package ltd.vastchain.bluetooth

import android.annotation.TargetApi
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import ltd.vastchain.jsbridge.util.LogUtil
import java.util.*
import kotlin.math.min


/**
 * Created by admin on 2021/9/13.
 */
class BluetoothPlugin(private var application: Application, private var context: Context) {

	private var mDeviceId: String = ""

	var blueListener: IBlueListener? = null
	private var read_UUID_chara: UUID? = null
	private var read_UUID_service: UUID? = null
	private var write_UUID_chara: UUID? = null
	private var write_UUID_service: UUID? = null
	private var notify_UUID_chara: UUID? = null
	private var notify_UUID_service: UUID? = null
	private var mBluetoothManager: BluetoothManager? = null
	private var mBluetoothAdapter: BluetoothAdapter? = null
	private var mBluetoothGatt: BluetoothGatt? = null
	private var mtu: Int = 20

	private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

	private var connectState = BluetoothProfile.STATE_DISCONNECTED
	private var listenRead = false
	var deviceIds :MutableList<String> =  mutableListOf<String>()
	var notifyLength = 0

	fun setUp() {
		mBluetoothManager =
			application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
		mBluetoothAdapter = mBluetoothManager?.adapter
	}

	@TargetApi(21)
	@Throws(IllegalStateException::class)
	fun startScan21(timeOut: Long = 0) {
		deviceIds.clear()
		if (mBluetoothAdapter == null) {
			blueListener?.setUpFail()
			LogUtil.e("请先初始化蓝牙模块")
			return
		}
		mBluetoothAdapter?.let {
			val scanner = it.bluetoothLeScanner
				?: throw IllegalStateException("getBluetoothLeScanner() is null. Is the Adapter on?")
			val settings =
				ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
			scanner.startScan(null, settings, getScanCallback21())
		}
		if (timeOut != 0L) {
			Handler(Looper.getMainLooper()).postDelayed({
				LogUtil.e("超时停止扫描")
				stopScan21()
			}, timeOut)
		}
	}

	private var scanCallback21: ScanCallback? = null

	@TargetApi(21)
	private fun getScanCallback21(): ScanCallback? {
		if (scanCallback21 == null) {
			scanCallback21 = object : ScanCallback() {
				override fun onScanResult(callbackType: Int, result: ScanResult) {
					super.onScanResult(callbackType, result)
//					LogUtil.e(result.device.address)
					if (result.device.name != null && result.device.name.startsWith("VC")) {
						if (deviceIds.contains(result.device.address).not()) {
							deviceIds.add(result.device.address)
						}
						LogUtil.e("搜索到蓝牙设备")
						LogUtil.e(result.device.name)
						LogUtil.e(result.device.address)
						this@BluetoothPlugin.blueListener?.scanResult(
							result.device.address,
							result.device.name
						)
					}
				}

				override fun onBatchScanResults(results: List<ScanResult>) {
//					LogUtil.e("搜索到蓝牙设备size:" + results.size)
//					results?.forEach { item->
//						if (item.device.name != null && item.device.name.endsWith("2034")) {
//							LogUtil.e("搜索到蓝牙设备")
//							LogUtil.e(item.device.name)
//							LogUtil.e(item.device.address)
//							this@BluetoothPlugin.blueListener?.scanResult(item.device.address, item.device.name)
//						}
//					}
					super.onBatchScanResults(results)
				}

				override fun onScanFailed(errorCode: Int) {
					super.onScanFailed(errorCode)
				}
			}
		}
		return scanCallback21
	}


	@TargetApi(21)
	fun stopScan21() {
		if (mBluetoothAdapter == null) {
			blueListener?.setUpFail()
			LogUtil.e("请先初始化蓝牙模块")
			return
		}
		if (deviceIds.isEmpty()) {
			blueListener?.scanFail()
		}
		val scanner = mBluetoothAdapter?.bluetoothLeScanner
		scanner?.stopScan(getScanCallback21())
	}

	fun connect(deviceId: String?, timeOut: Long = 0) {
		if (timeOut != 0L) {
			mHandler.postDelayed({
				if (connectState == BluetoothProfile.STATE_DISCONNECTED) {
					blueListener?.connectFail(errorCode = -1, message = "连接超时")
				}
			}, timeOut)
		}
		connect(deviceId)
	}

	private fun connect(deviceId: String?) {
		if (mBluetoothAdapter == null) {
			blueListener?.setUpFail()
			LogUtil.e("请先初始化蓝牙模块")
			return
		}
		deviceId ?: return
		this.mDeviceId = deviceId
		val device = mBluetoothAdapter?.getRemoteDevice(deviceId)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mBluetoothGatt = device?.connectGatt(
				context,
				false,
				mGattCallback,
				BluetoothDevice.TRANSPORT_LE
			)
		} else {
			mBluetoothGatt =
				device?.connectGatt(context, false, mGattCallback)
		}
	}

	fun disconnect() {
		if (mBluetoothGatt == null) {
			blueListener?.unConnect()
			return
		}
		mBluetoothGatt?.disconnect()
	}

	fun writeCharacteristic(
		data: String,
		length: Int
	) {
		if (mBluetoothAdapter == null) {
			blueListener?.setUpFail()
			LogUtil.e("请先初始化蓝牙模块")
			return
		}
		this.notifyLength = length
		if (mBluetoothGatt == null) {
			blueListener?.unConnect()
			return
		}
		var characteristic: BluetoothGattCharacteristic? =
			mBluetoothGatt?.getService(write_UUID_service)?.getCharacteristic(write_UUID_chara)

		if (characteristic == null) {
			LogUtil.e("characteristic 为空")
			return
		}

		var sendData: ByteArray = BluetoothUtils.getHexBytes(data)

		var splice = 0
		splice = if (sendData.size % mtu == 0) {
			sendData.size / mtu
		} else {
			sendData.size / mtu + 1
		}


		for (i in 0 until splice) {
			LogUtil.e("分包发送,包index:$i")
			LogUtil.e("分包发送,包begin:${i*mtu}")
			LogUtil.e("分包发送,包end:${min((i + 1) * mtu, sendData.size)}")
			var spliceData = sendData.copyOfRange(i * mtu, min((i + 1) * mtu, sendData.size))
			if (characteristic.setValue(spliceData).not()) {
				blueListener?.writeFail("设置数据出错，包index:$i")
				return
			}

			// Apply the correct write type
//		if (request.getWriteType() === Protos.WriteCharacteristicRequest.WriteType.WITHOUT_RESPONSE) {
			characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
//		} else {
//		characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
//		}
			if (mBluetoothGatt?.writeCharacteristic(characteristic) == false) {
				blueListener?.writeFail("写入数据出错，包index:$i")
				return
			}
			Thread.sleep(200)
		}
		blueListener?.writeSuccess()
	}

	fun readCharacteristic() {
		if (mBluetoothAdapter == null) {
			LogUtil.e("请先初始化蓝牙模块")
			blueListener?.setUpFail()
			return
		}
		if (mBluetoothGatt == null) {
			blueListener?.unConnect()
			return
		}
		listenRead = true

//		var characteristic: BluetoothGattCharacteristic? =
//			mBluetoothGatt?.getService(read_UUID_service)?.getCharacteristic(read_UUID_chara)
//
//		if (mBluetoothGatt?.readCharacteristic(characteristic) == true) {
//			LogUtil.e("读取数据成功")
//		} else {
//			LogUtil.e("读取数据失败")
//		}
	}


	fun setMtu(mtu: Int?) {
		mtu ?: return
		this.mtu = mtu
	}

	private fun initServiceAndChara() {
		val bluetoothGattServices = mBluetoothGatt!!.services
		for (bluetoothGattService in bluetoothGattServices) {
			val characteristics = bluetoothGattService.characteristics
			for (characteristic in characteristics) {
				val charaProp = characteristic.properties
				if (charaProp and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
					read_UUID_chara = characteristic.uuid
					read_UUID_service = bluetoothGattService.uuid
					LogUtil.e(
						"read_chara=" + read_UUID_chara.toString() + "----read_service=" + read_UUID_service
					)
				}
				if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
					write_UUID_chara = characteristic.uuid
					write_UUID_service = bluetoothGattService.uuid
					LogUtil.e(
						"write_chara=" + write_UUID_chara.toString() + "----write_service=" + write_UUID_service
					)
				}
				if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
					write_UUID_chara = characteristic.uuid
					write_UUID_service = bluetoothGattService.uuid
					LogUtil.e(
						"write_chara=" + write_UUID_chara.toString() + "----write_service=" + write_UUID_service
					)
				}
				if (charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
					notify_UUID_chara = characteristic.uuid
					notify_UUID_service = bluetoothGattService.uuid
					LogUtil.e(
						"notify_chara=" + notify_UUID_chara.toString() + "----notify_service=" + notify_UUID_service
					)
				}
//				if (charaProp and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
//					indicate_UUID_chara = characteristic.uuid
//					indicate_UUID_service = bluetoothGattService.uuid
//					LogUtil.e(
//						"indicate_chara=" + indicate_UUID_chara.toString() + "----indicate_service=" + indicate_UUID_service
//					)
//				}
			}
		}
	}

	fun isBleSupported(): Boolean {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && application
			.packageManager.hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE
			)
	}

	fun isEnabled(): Boolean {
		return mBluetoothAdapter!!.isEnabled
	}

	fun release() {

	}

	private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
		override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
//			val time: Long = System.currentTimeMillis() - lastTime
//			if (newState == BluetoothProfile.STATE_CONNECTED) {
//				LogUtil.e( "连接消耗时间：" + time + "毫秒")
//			}
			LogUtil.e("[onConnectionStateChange] status: $status newState: $newState")
			if (status == 0 || status == 2) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					connectState = BluetoothProfile.STATE_CONNECTED
					mBluetoothGatt?.discoverServices()
				}
				if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					blueListener?.disconnectSuccess()
					gatt.close()
				}
			}
			if (status == 133) {
//				mDevices.remove(gatt.device.address)
//				blueListener?.connectFail(status, "蓝牙设备可能还没关闭")
				gatt.close()
				reconnect()
//				val flag: Boolean = reconnect(gatt.device.address)
//				if (!flag) {
//					invokeMethodUIThread(
//						"DeviceState",
//						ProtoMaker.from(gatt.device, newState).toByteArray()
//					)
//				}
			}
		}

		override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
			LogUtil.e("onServicesDiscovered")
			initServiceAndChara()
//			mBluetoothGatt?.requestMtu(mtu + 3)

			var flag = BluetoothUtils.enableNotification(
				mBluetoothGatt,
				true,
				mBluetoothGatt!!.getService(notify_UUID_service)
					.getCharacteristic(notify_UUID_chara)
			)
			blueListener?.connectSuccess()
			if (flag) {
				LogUtil.e("设置通知成功")
			}
		}

		override fun onCharacteristicRead(
			gatt: BluetoothGatt,
			characteristic: BluetoothGattCharacteristic,
			status: Int
		) {
			LogUtil.e("收到蓝牙数据返回1")
			// For all other profiles, writes the data formatted in HEX.
			val data = characteristic.value
			if (data != null && data.isNotEmpty()) {
				val stringBuilder = StringBuilder(data.size)
				for (byteChar in data) stringBuilder.append(String.format("%02X ", byteChar))
				LogUtil.e("数据:$stringBuilder")
			}
		}

		private var receiveData: StringBuilder = StringBuilder()
		override fun onCharacteristicChanged(
			gatt: BluetoothGatt?,
			characteristic: BluetoothGattCharacteristic?
		) {
			super.onCharacteristicChanged(gatt, characteristic)
			// 调用方设置了回调
			if (listenRead) {
				val data = characteristic?.value
				if (data != null && data.isNotEmpty()) {
					val stringBuilder = StringBuilder()
					for (byteChar in data) stringBuilder.append(String.format("%02X", byteChar))
					receiveData.append(stringBuilder)
					LogUtil.e("数据:$stringBuilder")
				}

				if (receiveData.length >= notifyLength * 2) {
					// 字符返回
					blueListener?.readCallBack(receiveData.substring(0, notifyLength * 2))
					receiveData.clear()
				}
			}
		}

		override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
			super.onMtuChanged(gatt, mtu, status)
			LogUtil.e("mtu大小改变" + mtu + "staus:" + status)
			if (status == 0) {
				blueListener?.setMtuSuccess()
				var flag = BluetoothUtils.enableNotification(
					mBluetoothGatt,
					true,
					mBluetoothGatt!!.getService(notify_UUID_service)
						.getCharacteristic(notify_UUID_chara)
				)
				if (flag) {
					LogUtil.e("设置通知成功")
				}
			} else {
				blueListener?.setMtuFail()
			}
		}
	}

	private val count = 3
	private var tryCount = 0

	private fun reconnect(): Boolean {
		LogUtil.e("重试几次")
		tryCount++
		if (tryCount > count) {
			blueListener?.connectFail(errorCode = 133, message = "重试3次仍然失败")
			return false
		}
		connect(this.mDeviceId)
		return true
	}

}