package ltd.vastchain.jsbridge.util

import android.util.Log
import ltd.vastchain.jsbridge.BuildConfig


/**
 * 日志相关工具类
 */
object LogUtil {
	private const val MAX_DAYS = 7 * 24 * 3600 * 1000L
	private const val DEFAULT_TAG = "cxd"


	@JvmStatic
	fun d(msg: String) {
		d(DEFAULT_TAG, msg)
	}

	@JvmStatic
	fun d(tag: String, msg: String) {
		log(tag, msg, null, Log.DEBUG)
	}

	@JvmStatic
	fun d(t: Throwable) {
		log(DEFAULT_TAG, null, t, Log.DEBUG)
	}

	@JvmStatic
	fun i(msg: String) {
		i(DEFAULT_TAG, msg)
	}

	@JvmStatic
	fun i(tag: String, msg: String) {
		log(tag, msg, null, Log.INFO)
	}

	@JvmStatic
	fun i(t: Throwable) {
		log(DEFAULT_TAG, null, t, Log.INFO)
	}

	@JvmStatic
	fun i(tag: String, t: Throwable) {
		log(tag, null, t, Log.INFO)
	}

	@JvmStatic
	fun w(msg: String) {
		w(DEFAULT_TAG, msg)
	}

	@JvmStatic
	fun w(tag: String, msg: String) {
		log(tag, msg, null, Log.WARN)
	}

	@JvmStatic
	fun w(t: Throwable) {
		log(DEFAULT_TAG, null, t, Log.WARN)
	}

	@JvmStatic
	fun e(msg: String) {
		e(DEFAULT_TAG, msg)
	}

	@JvmStatic
	fun e(tag: String, msg: String) {
		log(tag, msg, null, Log.ERROR)
	}

	@JvmStatic
	fun e(t: Throwable) {
		log(DEFAULT_TAG, null, t, Log.ERROR)
	}

	@JvmStatic
	fun e(tag: String, t: Throwable) {
		log(tag, null, t, Log.ERROR)
	}

	@JvmStatic
	private fun log(tag: String, msg: String?, t: Throwable?, priority: Int) {
		if (!BuildConfig.DEBUG) return
		if (msg.isNullOrBlank() && t == null) return
		val log = getLogString(msg, t)
		when (priority) {
			Log.INFO -> Log.i(tag, log)
			Log.WARN -> Log.w(tag, log)
			Log.ERROR -> Log.e(tag, log)
			else -> Log.d(tag, log)
		}
	}

	@JvmStatic
	private fun getLogString(msg: String?, t: Throwable?): String {
		var log = ""
		if (msg != null) log += msg
		if (t != null) {
			if (log.isNotBlank()) {
				log += "\n"
			}
			log += Log.getStackTraceString(t)
		}
		return log
	}
}