package ltd.vastchain.bluetooth.data

/**
 * Created by admin on 2022/2/22.
 */
object UrlConstants {
    const val STOREHOUSE_HOME = "/softwareScan"
    const val STOREHOUSE_SELL_OUT = "/storehouse/basic/exWarehousingSell"
    const val STOREHOUSE_BACK = "/storehouse/basic/useBackWarehousing"

    private val BACK_CONFIRM_URL = mutableListOf<String>()
    init {
        BACK_CONFIRM_URL.apply {
//            add(STOREHOUSE_SELL_OUT)
            add(STOREHOUSE_BACK)
        }
    }

    fun checkInUrl(url: String):Boolean {
        val item  = BACK_CONFIRM_URL.find { url.contains(it) }
        if(item != null ) {
            return true
        }
        return false
    }
}