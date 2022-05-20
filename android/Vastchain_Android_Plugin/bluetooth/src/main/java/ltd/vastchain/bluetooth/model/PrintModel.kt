package ltd.vastchain.bluetooth.model

/**
 * Created by admin on 2021/12/30.
 */
data class PrintModel(
    val url: String?,
    val qrCodeId: String?,
    val name: String?,
    val packageCount: String?,
    val totalCount: String?,
    val orgName: String?,
    val storehouseName: String?,
    val storehouseOrgName: String?
) {

    fun isWarehouse(): Boolean {
        return !storehouseName.isNullOrEmpty()
    }

    fun isConfigCommodity(): Boolean {
        return !name.isNullOrEmpty()
    }
}