package ltd.vastchain.bluetooth

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.content.Context
import ltd.vastchain.bluetooth.model.PrintModel

/**
 * Created by admin on 2021/12/30.
 */
interface IBluePlugin {

    fun isBleSupported(): Boolean

    fun isEnabled(): Boolean

    fun setUp()

    fun startScan21(timeOut: Long = 0)

    fun stopScan21()

    fun connect(deviceId: String?, timeOut: Long = 0)

    fun disconnect()

    fun writeCharacteristic(
        data: String,
        length: Int
    )

    fun readCharacteristic()

    fun setMtu(mtu: Int?)

    fun getBondedDevices()

    fun print(deviceId: String, data: PrintModel)

    fun setBlueListener(listener: IBlueListener?)
}