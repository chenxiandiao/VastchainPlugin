package ltd.vastchain.bluetooth

import android.bluetooth.BluetoothDevice

/**
 * Created by admin on 2021/9/14.
 */
interface IBlueListener {

	fun setUpFail()

	fun unConnect()

	fun scanResult(address: String, name: String?)

	fun scanStop()

	fun scanStopByTimeOut()

	fun connectSuccess()

	fun connectFail(errorCode: Int, message: String)

	fun disconnectSuccess()

	fun writeSuccess()

	fun writeFail(message: String)

	fun readCallBack(data: String)

	fun setMtuSuccess()

	fun setMtuFail()

	fun getBondedDevicesResult(device: Set<BluetoothDevice>?)

	fun printSuccess()

	fun printFail()
}