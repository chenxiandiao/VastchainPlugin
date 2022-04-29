package ltd.vastchain.bluetooth

import org.json.JSONObject

/**
 * Created by admin on 2022/1/5.
 */
interface IWebViewListener {

    fun openActivity(targetName: String?)

    fun openWareHouseActivity(
        qrCodeId: String,
        type: String,
        commodityId: String,
        sceneFunctionType: String
    )

    fun openWareHouseActivity(params: JSONObject)

    fun getDataFromApp(type: String, data: JSONObject? = null): JSONObject
}