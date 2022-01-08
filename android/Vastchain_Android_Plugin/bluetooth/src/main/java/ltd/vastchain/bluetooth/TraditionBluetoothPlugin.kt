package ltd.vastchain.bluetooth

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import ltd.vastchain.bluetooth.model.PrintModel
import ltd.vastchain.bluetooth.printer.PrinterUtil
import ltd.vastchain.jsbridge.util.LogUtil
import zpCPCLSDK.zpCPCLSDK.zp_cpcl_BluetoothPrinter

/**
 * Created by admin on 2021/12/30.
 */
class TraditionBluetoothPlugin(private var application: Application, private var context: Context): IBluePlugin {
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var blueListener: IBlueListener? = null
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    Log.e("cxd", deviceName.orEmpty())
                    Log.e("cxd", deviceHardwareAddress.orEmpty())

                    if (deviceName != null && (deviceName.startsWith("CT") && deviceName.endsWith("L").not())) {
                        blueListener?.scanResult(
                            deviceHardwareAddress!!,
                            deviceName
                        )
                    }
                }
            }
        }
    }

    override fun isBleSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && application
            .packageManager.hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE
            )
    }

    override fun isEnabled(): Boolean {
        return mBluetoothAdapter!!.isEnabled
    }

    override fun setUp() {
        mBluetoothManager =
            application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager?.adapter
    }

    override fun startScan21(timeOut: Long) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        mBluetoothAdapter?.startDiscovery()
    }

    override fun stopScan21() {
        mBluetoothAdapter?.cancelDiscovery()
    }

    override fun connect(deviceId: String?, timeOut: Long) {
        val zpSDK = zp_cpcl_BluetoothPrinter(context)
        if (zpSDK.connect(deviceId)) {
            zpSDK.disconnect()
            blueListener?.connectSuccess()
        } else {
            blueListener?.connectFail(-1, "pin码错误")
        }
    }

    override fun disconnect() {
//        val zpSDK = zp_cpcl_BluetoothPrinter(context)
//        zpSDK.disconnect()
//        blueListener?.disconnectSuccess()
    }

    override fun writeCharacteristic(data: String, length: Int) {

    }

    override fun readCharacteristic() {

    }

    override fun setMtu(mtu: Int?) {

    }

    override fun getBondedDevices() {
        blueListener?.getBondedDevicesResult(mBluetoothAdapter?.bondedDevices)
    }

    override fun print(deviceId: String, data:PrintModel) {
        PrinterUtil.print(context, deviceId, data, this.blueListener)
    }

    override fun setBlueListener(listener: IBlueListener?) {
        this.blueListener = listener
    }


}