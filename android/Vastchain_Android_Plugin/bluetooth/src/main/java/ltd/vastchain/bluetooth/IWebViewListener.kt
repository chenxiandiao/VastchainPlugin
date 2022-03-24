package ltd.vastchain.bluetooth

/**
 * Created by admin on 2022/1/5.
 */
interface IWebViewListener {

    fun openActivity(targetName: String?)

    fun openWareHouseActivity(qrCodeId:String, type: String, commodityId: String, sceneFunctionType:String)
}