package ltd.vastchain.bluetooth

import android.bluetooth.BluetoothGattDescriptor

import android.bluetooth.BluetoothGattCharacteristic

import android.bluetooth.BluetoothGatt

import android.annotation.SuppressLint
import ltd.vastchain.jsbridge.util.LogUtil
import java.lang.StringBuilder
import java.util.*
import kotlin.experimental.and

//import kotlin.experimental.and


/**
 * Created by admin on 2021/9/13.
 */
object BluetoothUtils {

	const val NOTIFY_DESCRIPTOR: String = "00002902-0000-1000-8000-00805f9b34fb"
	/**
	 * 是否开启蓝牙的通知
	 *
	 * @param enable
	 * @param characteristic
	 * @return
	 */
	@SuppressLint("NewApi")
	fun enableNotification(
		bluetoothGatt: BluetoothGatt?,
		enable: Boolean,
		characteristic: BluetoothGattCharacteristic?
	): Boolean {
		if (bluetoothGatt == null || characteristic == null) {
			return false
		}
		if (!bluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
			return false
		}
		//获取到Notify当中的Descriptor通道  然后再进行注册
		val clientConfig =
			characteristic.getDescriptor(UUID.fromString(NOTIFY_DESCRIPTOR))
				?: return false
		if (enable) {
			clientConfig.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
		} else {
			clientConfig.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
		}
		return bluetoothGatt.writeDescriptor(clientConfig)
	}

	/**
	 * 将字节 转换为字符串
	 *
	 * @param src 需要转换的字节数组
	 * @return 返回转换完之后的数据
	 */
	fun bytesToHexString(src: ByteArray?): String? {
		val stringBuilder = StringBuilder("")
		if (src == null || src.size <= 0) {
			return null
		}
		for (i in src.indices) {
			val v: Byte = (src[i] and 0xFF.toByte())
			val hv = Integer.toHexString(v.toInt())
			if (hv.length < 2) {
				stringBuilder.append(0)
			}
			stringBuilder.append(hv)
		}
		return stringBuilder.toString()
	}

	/**
	 * 将字符串转化为16进制的字节
	 *
	 * @param message
	 * 需要被转换的字符
	 * @return
	 */
	fun getHexBytes(message: String): ByteArray {
		val len = message.length / 2
		val chars = message.toCharArray()
		val hexStr = arrayOfNulls<String>(len)
		val bytes = ByteArray(len)
		var i = 0
		var j = 0
		while (j < len) {
			hexStr[j] = "" + chars[i] + chars[i + 1]
			bytes[j] = hexStr[j]!!.toInt(16).toByte()
			i += 2
			j++
		}
		return bytes
	}
}