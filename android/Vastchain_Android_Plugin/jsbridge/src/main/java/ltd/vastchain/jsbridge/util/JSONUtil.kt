package ltd.vastchain.jsbridge.util

import org.json.JSONObject

/**
 * Created by admin on 2021/9/14.
 */
object JSONUtil {

	fun success(code: Int = 0, message: String? = null):JSONObject {
		var res = JSONObject()
		res.put("code", code)
		if (message.isNullOrEmpty()) {
			res.put("message", "成功")
		} else {
			res.put("message", message)
		}

		return res
	}

	fun error(code: Int = -1, message: String? = null):JSONObject {
		var res = JSONObject()
		res.put("code", code)
		if (message.isNullOrEmpty()) {
			res.put("message", "失败")
		} else {
			res.put("message", message)
		}

		return res
	}
}