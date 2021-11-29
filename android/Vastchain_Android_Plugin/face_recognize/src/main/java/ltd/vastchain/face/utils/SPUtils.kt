package ltd.vastchain.face.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Created by admin on 2021/11/23.
 */
object SPUtils {
    const val FACE = "Face"
    private var context: Context? = null

    private val mPrefs by lazy {
        Log.e("cxd", "请先设置context111")
        context?.getSharedPreferences(FACE, Context.MODE_PRIVATE)
    }

    fun initContext(context: Context) {
        this.context = context
    }

    fun getString(key: String):String {
//        val preferences = context?.getSharedPreferences(FACE, Context.MODE_PRIVATE)
//        return  preferences?.getString(key,"").orEmpty()
        return mPrefs?.getString(key,"").orEmpty()
    }

     fun <T> put(key: String, value: T) {
        mPrefs?.edit()?.apply {
            when (value) {
                is String -> putString(key, value)
//                is Boolean -> putBoolean(key, value)
//                is Int -> putInt(key, value)
//                is Long -> putLong(key, value)
//                is Float -> putFloat(key, value)
//                is Serializable -> putSerializable(key, value)
//                else -> {
//                    IllegalArgumentException("SharedPreferences can't save this type: $value").printStackTrace()
//                }
            }
        }?.commit()
    }
}