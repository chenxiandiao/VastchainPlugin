package ltd.vastchain.bluetooth.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Created by admin on 2022/1/7.
 */
object ClipUtils {

    fun copyText(context: Context, text: String?) {
        //1. 复制字符串到剪贴板管理器
        val cmb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText(null, text))
    }
}