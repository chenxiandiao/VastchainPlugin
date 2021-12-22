package ltd.vastchain.camera.record

import android.net.Uri

/**
 * Created by admin on 2021/12/21.
 */
interface IRecordListener {

    fun success(uri: Uri?, duration: String)
}