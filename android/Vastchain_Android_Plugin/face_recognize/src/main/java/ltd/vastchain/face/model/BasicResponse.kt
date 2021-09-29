package ltd.vastchain.face.model

/**
 * Created by admin on 2021/9/28.
 */
data class BasicResponse(
	val code: String,
	val msg: String
) {
	fun success():Boolean {
		if (code == "Ok" || code == "Pass") {
			return true
		}
		return false
	}
}
