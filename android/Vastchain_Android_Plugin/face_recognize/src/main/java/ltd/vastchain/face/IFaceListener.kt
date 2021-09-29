package ltd.vastchain.face

/**
 * Created by admin on 2021/9/28.
 */
interface IFaceListener {

	fun compareFail()

	fun compareSuccess()

	fun eyeCheckFail()

	fun eyeCheckSuccess()

	fun mouthCheckFail()

	fun mouthCheckSuccess()
}