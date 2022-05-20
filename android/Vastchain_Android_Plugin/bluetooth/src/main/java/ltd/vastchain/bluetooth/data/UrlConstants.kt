package ltd.vastchain.bluetooth.data

/**
 * Created by admin on 2022/2/22.
 */
object UrlConstants {
    const val STOREHOUSE_HOME = "/softwareScan"
//   领用回库
    private const val STOREHOUSE_BACK = "/storehouse/basic/useBackWarehousing"
    private const val STOREHOUSE_RETURN_GOODS = "/storehouse/basic/returnGoods"
    private const val STOREHOUSE_MINBAO_Receive = "/storehouse/basic/componiesReceive"
    private const val STOREHOUSE_MINBAO_USE = "/storehouse/basic/workspaceUse"
    private const val STOREHOUSE_MINBAO_BACK = "/storehouse/basic/returnTailing"

    private val BACK_CONFIRM_URL = mutableListOf<String>()
    init {
        BACK_CONFIRM_URL.apply {
//            add(STOREHOUSE_SELL_OUT)
            add(STOREHOUSE_BACK)
            add(STOREHOUSE_RETURN_GOODS)
            add(STOREHOUSE_MINBAO_Receive)
            add(STOREHOUSE_MINBAO_USE)
            add(STOREHOUSE_MINBAO_BACK)
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