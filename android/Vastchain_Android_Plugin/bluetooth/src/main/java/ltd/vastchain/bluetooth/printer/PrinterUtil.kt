package ltd.vastchain.bluetooth.printer

import android.content.Context
import android.util.Log
import android.widget.Toast
import ltd.vastchain.bluetooth.model.PrintModel
import zpCPCLSDK.zpCPCLSDK.zp_cpcl_BluetoothPrinter

/**
 * Created by admin on 2021/12/29.
 */
object PrinterUtil {

    var openPrinter = true

    fun print(context: Context, address: String?, data: PrintModel) {
        if (openPrinter.not()) {
            return
        }
        val zpSDK = zp_cpcl_BluetoothPrinter(context)
        Log.e("Printer", address.orEmpty())
        if (!zpSDK.connect(address)) {
            Toast.makeText(context, "连接失败------", Toast.LENGTH_LONG).show()
            return
        }
        zpSDK.pageSetup(600, 300)
        zpSDK.drawQrCode(30, 40, data.url, 0, 7, 0)
        if (data.qrCodeId.isNullOrEmpty().not()) {
            zpSDK.drawText(280, 47, data.qrCodeId, 3, 0, 1, false, false)
        }
        if (data.name.isNullOrEmpty().not()) {
            zpSDK.drawText(280, 89, data.name, 3, 0, 1, false, false)
        }
        if (data.packageCount.isNullOrEmpty().not()) {
            zpSDK.drawText(280, 132, data.packageCount, 3, 0, 0, false, false)
        }
        if (data.totalCount.isNullOrEmpty().not()) {
            zpSDK.drawText(280, 169, data.totalCount, 3, 0, 0, false, false)
        }
        if (data.orgName.isNullOrEmpty().not()) {
            zpSDK.drawText(280, 211, data.orgName, 3, 0, 0, false, false)
        }

        zpSDK.print(0, 0)
        zpSDK.disconnect()
    }
}