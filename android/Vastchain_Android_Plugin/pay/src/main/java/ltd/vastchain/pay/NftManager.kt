package ltd.vastchain.pay

import android.content.Context
import android.content.Intent

/**
 * Created by admin on 2022/4/29.
 */
object NftManager {

    const val APP_ID = "appId"
    const val TITLE = "title"
    const val URL = "url"

    fun start(context: Context, appId: String, url:String, title: String ) {
        val intent = Intent(context, PayWebActivity::class.java)
        intent.putExtra(APP_ID, appId)
        intent.putExtra(TITLE, title)
        intent.putExtra(URL, url)
        context.startActivity(intent)
    }
}