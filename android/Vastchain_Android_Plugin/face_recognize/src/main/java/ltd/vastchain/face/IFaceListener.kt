package ltd.vastchain.face

/**
 * Created by admin on 2021/9/28.
 */
interface IFaceListener {

	fun beginCompare()

	fun checkFace();

	fun compareFail(msg: String)

	fun compareSuccess()

	fun beginEyeCheck()

	fun eyeCheckFail()

	fun eyeCheckSuccess()

	fun beginMouthCheck()

	fun mouthCheckFail()

	fun mouthCheckSuccess()

	fun compareEnd()
}